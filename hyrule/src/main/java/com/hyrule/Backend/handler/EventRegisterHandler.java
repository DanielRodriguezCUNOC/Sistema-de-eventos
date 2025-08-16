package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.event.EventModel;
import com.hyrule.Backend.model.event.EventType;
import com.hyrule.Backend.persistence.event.ControlEvent;
import com.hyrule.interfaces.RegisterHandler;

public class EventRegisterHandler implements RegisterHandler {

    // *Expresion regular para validar el registro de eventos */
    private static final Pattern PATRON = Pattern.compile(
            "^REGISTRO_EVENTO\\s*\\(\\s*\"(EVT-\\d{8})\"\\s*,\\s*\"(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})\"\\s*,"
                    +
                    "\\s*\"(CHARLA|CONGRESO|TALLER|DEBATE)\"\\s*,\\s*\"([a-zA-ZÁÉÍÓÚáéíóúÑñ0-9\\-\\s]+)\"\\s*,\\s*\"([a-zA-ZÁÉÍÓÚáéíóúÑñ0-9.,()\\-\\s]{1,150})\"\\s*,\\s*(\\d+)\\s*,\\s*(\\d+(\\.\\d{1,2})?)\\s*\\);$");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // * Creamos una instacia para ingresar los datos a la BD */
    private final ControlEvent control = new ControlEvent();

    // *Creamos un almacenamiento interno para detectar eventos */
    private final Set<String> archiveCodes = new HashSet<>();
    private final Set<String> archiveTitles = new HashSet<>();

    // *Metodo para procesar el registro de eventos desde el archivo */
    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {
            // *Validamos la linea con la expresion regular */
            Matcher m = PATRON.matcher(linea.trim());
            if (!m.matches())
                return false;

            String codigo = m.group(1);
            LocalDate fecha = LocalDate.parse(m.group(2) + "/" + m.group(3) + "/" + m.group(4), DATE_FORMAT);
            EventType tipo = EventType.valueOf(m.group(5));
            String titulo = m.group(6);
            String ubicacion = m.group(7);
            Integer cupo = Integer.parseInt(m.group(8));
            BigDecimal costo = new BigDecimal(m.group(9));

            // *Creamos una instancia de la tabla Evento */
            EventModel evento = new EventModel(codigo, fecha, tipo, titulo, ubicacion, cupo, costo);

            // *Verificamos duplicados */
            if (archiveCodes.contains(codigo)) {
                logWriter.write("Código de evento duplicado en el archivo: " + codigo);
                logWriter.newLine();
                return false;
            }
            if (archiveTitles.contains(titulo + "_" + fecha)) {
                logWriter.write("Título de evento duplicado en el archivo: " + titulo);
                logWriter.newLine();
                return false;
            }

            // *Agregamos los datos al HashSet */
            archiveCodes.add(codigo);
            archiveTitles.add(titulo + "_" + fecha);

            logWriter.write("Evento registrado: " + evento);
            logWriter.newLine();

            // * Insertamos el evento en la base de datos */
            return control.insert(evento) != null;
        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando REGISTRO_EVENTO: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    public boolean insertFromForm(EventModel event) {
        return control.insert(event) != null;
    }

    public String validateForm(EventModel event, String fechaStr, String costoStr) {
        if (event == null) {
            return "El evento no puede ser nulo.";
        }
        if (!validateDataIntegrity(event, fechaStr, costoStr)) {
            return "Datos del evento inválidos.";
        }

        String validation = control.validateEvent(event);
        if (!"Ok".equals(validation)) {
            return validation;

        }

        return "Ok";

    }

    // *Validamos la integridad de los datos del formulario */
    private boolean validateDataIntegrity(EventModel evento, String fechaStr, String costoStr) {

        boolean codigoValido = evento.getCodigoEvento() != null &&
                evento.getCodigoEvento().matches("^EVT-\\d{8}$");

        boolean fechaValida = fechaStr != null && fechaStr.matches(
                "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$");

        boolean tipoValido = evento.getTipoEvento() != null &&
                evento.getTipoEvento().name().matches("^(CHARLA|CONGRESO|TALLER|DEBATE)$");

        boolean tituloValido = evento.getTituloEvento() != null &&
                evento.getTituloEvento().matches("^[a-zA-ZÁÉÍÓÚáéíóúÑñ0-9\\-\\s]{1,150}$");

        boolean ubicacionValida = evento.getUbicacionEvento() != null &&
                evento.getUbicacionEvento().matches("^[a-zA-ZÁÉÍÓÚáéíóúÑñ0-9.,()\\-\\s]{1,150}$")
                && !evento.getUbicacionEvento().isBlank();

        boolean cupoValido = evento.getCupoMaxParticipantes() != null &&
                evento.getCupoMaxParticipantes() > 0;

        boolean costoValido = costoStr != null &&
                costoStr.matches("^\\d+(\\.\\d{1,2})?$");

        return codigoValido && fechaValida && tipoValido && tituloValido &&
                ubicacionValida && cupoValido && costoValido;
    }

}
