package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.activity.ActivityModel;
import com.hyrule.Backend.model.activity.ActivityType;
import com.hyrule.Backend.persistence.activity.ControlActivity;
import com.hyrule.interfaces.RegisterHandler;

public class ActivityRegisterHandler implements RegisterHandler {

    // *Expresion regular para validacion */

    private static final Pattern PATRON = Pattern.compile(
            "^REGISTRO_ACTIVIDAD\\s*\\(\\s*\"(ACT-\\d{8,})\"\\s*,\\s*\"(EVT-\\d{8,})\"\\s*,\\s*\"(CHARLA|TALLER|DEBATE|OTRA)\"\\s*,\\s*\"([a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ.,:;()\\-\\s]{1,200})\"\\s*,\\s*\"([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\"\\s*,\\s*\"(([0-1]\\d|2[0-3]):[0-5]\\d)\"\\s*,\\s*\"(([0-1]\\d|2[0-3]):[0-5]\\d)\"\\s*,\\s*(\\d+)\\s*\\);$");

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    // * Creamos una instancia para ingresar los datos a la BD */
    private final ControlActivity control = new ControlActivity();

    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {
            Matcher m = PATRON.matcher(linea.trim());
            if (!m.matches())
                return false;
            String codigoActividad = m.group(1);
            String codigoEvento = m.group(2);
            ActivityType tipoActividad = ActivityType.valueOf(m.group(3));
            String descripcion = m.group(4);
            String correo = m.group(5);
            LocalTime horaInicio = LocalTime.parse(m.group(6), TIME_FORMAT);
            LocalTime horaFin = LocalTime.parse(m.group(7), TIME_FORMAT);
            int cupoMaximo = Integer.parseInt(m.group(8));

            // *Creamos una instancia de la tabla Actividad */
            ActivityModel actividad = new ActivityModel(codigoActividad, codigoEvento, tipoActividad, descripcion,
                    correo, horaInicio, horaFin, cupoMaximo);
            // * Insertamos la actividad en la base de datos */
            return control.insert(actividad) != null;

        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando REGISTRO_ACTIVIDAD: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    // *Registrar Actividad desde formulario y validar con la expresion regular */

    public boolean isValid(ActivityModel actividad) {
        try {
            if (actividad == null) {
                return false;
            }

            // *Validamos la integridad de los datos */
            if (!validateDataIntegrity(actividad)) {
                return false;
            }

            // *Insertamos la actividad en la base de datos */
            return control.insert(actividad) != null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    // *Validamos la integridad de los datos */
    private boolean validateDataIntegrity(ActivityModel actividad) {

        String codigoActividad = actividad.getCodigoActividad();
        String codigoEvento = actividad.getCodigoEvento();
        String descripcion = actividad.getDescripcion();
        String correo = actividad.getCorreo();
        LocalTime horaInicio = actividad.getHoraInicio();
        LocalTime horaFin = actividad.getHoraFin();
        int cupoMaximo = actividad.getCupoMaximo();

        return codigoActividad != null && !codigoActividad.isEmpty() &&
                codigoEvento != null && !codigoEvento.isEmpty() &&
                descripcion != null && !descripcion.isEmpty() &&
                correo != null && !correo.isEmpty() &&
                horaInicio != null && horaFin != null &&
                cupoMaximo > 0 &&
                Pattern.matches("^ACT-\\d{8}$", codigoActividad) &&
                Pattern.matches("^EVT-\\d{8}$", codigoEvento) &&
                Pattern.matches("^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ.,:;()\\-\\s]{1,200}$", descripcion) &&
                Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", correo) &&
                Pattern.matches("^([0-1]\\d|2[0-3]):[0-5]\\d$", horaInicio.format(TIME_FORMAT)) &&
                Pattern.matches("^([0-1]\\d|2[0-3]):[0-5]\\d$", horaFin.format(TIME_FORMAT)) &&
                Pattern.matches("^\\d+$", String.valueOf(cupoMaximo));
    }

}
