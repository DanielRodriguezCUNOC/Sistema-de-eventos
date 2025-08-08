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
            "^REGISTRO_EVENTO\\(\"(EVT-\\d{3})\",\"(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})\"," +
                    "\"(CHARLA|CONGRESO|TALLER|DEBATE)\",\"([^\"]+)\",\"([^\"]{1,150})\",(\\d+)\\);$");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // * Creamos una instacia para ingresar los datos a la BD */
    private final ControlEvent control = new ControlEvent();

    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {
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

    public boolean registerEventFromForm(String codigo, String fechaStr, String tipoStr, String titulo,
            String ubicacion, int cupoMax) {
        try {
            LocalDate fecha = LocalDate.parse(fechaStr, DATE_FORMAT);
            EventType tipo = EventType.valueOf(tipoStr.toUpperCase());

            // *Creamos un objeto que representa la tabla en la BD */
            EventModel event = new EventModel(codigo, fecha, tipo, titulo, ubicacion, cupoMax);

            // *Enviamos los datos para insertarlos en la BD */
            return control.insert(event) != null;

        } catch (Exception e) {
            System.out.println("Error al registrar el evento desde el formulario: " + e.getMessage());
            return false;
        }
    }
}
