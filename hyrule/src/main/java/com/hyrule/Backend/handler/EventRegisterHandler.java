package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

            // *Creamos una instancia de la tabla Evento */
            EventModel evento = new EventModel(codigo, fecha, tipo, titulo, ubicacion, cupo);
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

    // *Metodo para registrar un evento desde el formulario*/

    public boolean isValid(EventModel evento) {
        try {
            if (evento == null) {
                return false;
            }

            // *Validamos la integridad de los datos */
            if (!validateDataIntegrity(evento)) {
                return false;
            }

            // *Insertamos el evento en la base de datos */
            return control.insert(evento) != null;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // *Validamos la integridad de los datos */
    private boolean validateDataIntegrity(EventModel evento) {
        return evento.getCodigoEvento() != null && !evento.getCodigoEvento().isEmpty()
                && evento.getFechaEvento() != null && evento.getTipoEvento() != null
                && evento.getTituloEvento() != null && !evento.getTituloEvento().isEmpty()
                && evento.getUbicacionEvento() != null && !evento.getUbicacionEvento().isEmpty()
                && evento.getCupoMaxParticipantes() != null && evento.getCupoMaxParticipantes() > 0
                && Pattern.compile("^EVT-\\d{8}$").matcher(evento.getCodigoEvento()).matches()
                && evento.getFechaEvento().format(DATE_FORMAT)
                        .matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$")
                && Pattern.compile("^(CHARLA|CONGRESO|TALLER|DEBATE)$").matcher(evento.getTipoEvento().name()).matches()
                && Pattern.compile("^[a-zA-ZÁÉÍÓÚáéíóúÑñ0-9\\-\\s]{1,150}$").matcher(evento.getTituloEvento()).matches()
                && Pattern.compile("^[a-zA-ZÁÉÍÓÚáéíóúÑñ0-9.,()\\-\\s]{1,150}$").matcher(evento.getUbicacionEvento())
                        .matches()
                && evento.getCupoMaxParticipantes() > 0;
    }
}
