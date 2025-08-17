package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import com.hyrule.Backend.RegularExpresion.RExpresionActividad;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.activity.ActivityModel;
import com.hyrule.Backend.persistence.activity.ControlActivity;
import com.hyrule.interfaces.RegisterHandler;

public class ActivityRegisterHandler implements RegisterHandler {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    // * Creamos una instancia para ingresar los datos a la BD */
    private final ControlActivity control = new ControlActivity();

    // *Estructura para el registro de actividades */
    ValidationArchive validator = ValidationArchive.getInstance();

    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {

            // *Validamos la linea con la expresion regular */
            RExpresionActividad parser = new RExpresionActividad();
            ActivityModel actividad = parser.parseEvent(linea.trim());
            if (actividad == null) {
                logWriter.write("Línea inválida o incompleta: " + linea);
                logWriter.newLine();
                return false;
            }

            // *Validamos si la actividad ya existe o si el participante es asistente */
            if (validator.existsActivity(actividad.getCodigoActividad())) {
                logWriter.write("La actividad ya existe: " + actividad.getCodigoActividad());
                logWriter.newLine();
                return false;

            }

            if (validator.isAsistente(actividad.getCorreo())) {
                logWriter.write("El participante esta registrado como asistente: " + actividad.getCorreo());
                logWriter.newLine();
                return false;
            }

            // *Agregamos los datos al HashSet */
            validator.addActividad(actividad);

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

    public boolean insertFromForm(ActivityModel activity) {
        try {
            return control.insert(activity) != null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String validateForm(ActivityModel activity) {
        if (activity == null) {
            return "La actividad no puede ser nula.";
        }
        if (!validateDataIntegrity(activity)) {
            return "Datos de la actividad inválidos.";
        }

        String validation = control.validateActivity(activity);
        if (!"Ok".equals(validation)) {
            return validation;

        }

        return "Ok";

    }

    // *Validamos la integridad de los datos del formulario */
    private boolean validateDataIntegrity(ActivityModel activity) {

        boolean codigoValido = activity.getCodigoActividad() != null &&
                activity.getCodigoActividad().matches("^ACT-\\d{8}$");

        boolean codigoEventoValido = activity.getCodigoEvento() != null &&
                activity.getCodigoEvento().matches("^EVT-\\d{8}$");

        boolean tipoValido = activity.getTipoActividad() != null &&
                activity.getTipoActividad().name().matches("^(CHARLA|CONGRESO|TALLER|DEBATE)$");

        boolean tituloValido = activity.getTituloActividad() != null &&
                activity.getTituloActividad().matches("^[a-zA-ZÁÉÍÓÚáéíóúÑñ0-9\\-\\s]{1,150}$");

        boolean correoValido = activity.getCorreo() != null &&
                activity.getCorreo().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

        boolean cupoValido = activity.getCupoMaximo() != null &&
                activity.getCupoMaximo() > 0;

        boolean horaInicioValida = activity.getHoraInicio() != null &&
                activity.getHoraInicio().format(TIME_FORMAT).matches("^([0-1]\\d|2[0-3]):[0-5]\\d$");

        boolean horaFinValida = activity.getHoraFin() != null &&
                activity.getHoraFin().format(TIME_FORMAT).matches("^([0-1]\\d|2[0-3]):[0-5]\\d$");

        return codigoValido && codigoEventoValido && tipoValido && tituloValido &&
                correoValido && cupoValido && horaInicioValida && horaFinValida;
    }

}
