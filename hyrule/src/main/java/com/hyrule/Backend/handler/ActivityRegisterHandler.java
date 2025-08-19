package com.hyrule.Backend.handler;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import com.hyrule.Backend.LogFormatter;
import com.hyrule.Backend.RegularExpresion.RExpresionActividad;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.activity.ActivityModel;
import com.hyrule.Backend.persistence.activity.ControlActivity;
import com.hyrule.interfaces.RegisterHandler;

/**
 * Manejador para el registro de actividades desde archivos y formularios.
 * Procesa validaciones, duplicados y persistencia de actividades de eventos.
 */
public class ActivityRegisterHandler implements RegisterHandler {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /** Controlador de persistencia para operaciones de actividades */
    private ControlActivity control = new ControlActivity();

    /** Conexión a la base de datos. */
    private Connection conn;

    /**
     * Constructor que recibe la conexión a la base de datos.
     */
    public ActivityRegisterHandler(Connection conn) {
        this.conn = conn;
    }

    /** Validador singleton para verificar duplicados y participantes */
    ValidationArchive validator = ValidationArchive.getInstance();

    /**
     * Procesa una línea de actividad desde archivo de carga masiva.
     * 
     * @param linea     línea del archivo con datos de la actividad
     * @param logWriter escritor para registrar errores y éxitos
     * @return true si la actividad se procesó correctamente
     */
    @Override
    public boolean process(String linea, LogFormatter logWriter) {
        try {

            // *Validamos la linea con la expresion regular */
            RExpresionActividad parser = new RExpresionActividad();
            ActivityModel actividad = parser.parseActivity(linea.trim());
            if (actividad == null) {
                logWriter.error("Línea inválida o incompleta: " + linea);
                return false;
            }

            // *Validamos si la actividad ya existe o si el participante es asistente */
            if (validator.existsActivity(actividad.getCodigoActividad())) {
                logWriter.error("La actividad ya existe: " + actividad.getCodigoActividad());
                return false;

            }

            if (!validator.existsEvent(actividad.getCodigoEvento())) {
                logWriter.error("El evento no existe: " + actividad.getCodigoEvento());
                return false;

            }

            if (validator.isAsistente(actividad.getCorreo())) {
                logWriter.error("El participante esta registrado como asistente: " + actividad.getCorreo());
                return false;
            }

            if (control.insert(actividad, conn) != null) {
                logWriter.info("Actividad registrada: " + actividad.getCodigoActividad());
                validator.addActividad(actividad);
                return true;
            } else {
                logWriter.error("No se pudo insertar la actividad: " + actividad.getCodigoActividad());
                return false;
            }

        } catch (Exception e) {
            try {
                logWriter.error("Excepción procesando REGISTRO_ACTIVIDAD: " + e.getMessage());
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    /**
     * Inserta una actividad directamente desde formulario.
     * 
     * @param activity la actividad a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertFromForm(ActivityModel activity) {
        try {
            return control.insert(activity, conn) != null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Valida los datos de una actividad desde formulario.
     * 
     * @param activity la actividad a validar
     * @return "Ok" si es válida, mensaje de error si no
     */
    public String validateForm(ActivityModel activity) {
        if (activity == null) {
            return "La actividad no puede ser nula.";
        }
        if (!validateDataIntegrity(activity)) {
            return "Datos de la actividad inválidos.";
        }

        String validation = control.validateActivity(activity, conn);
        if (!"Ok".equals(validation)) {
            return validation;

        }

        return "Ok";

    }

    /**
     * Valida la integridad de los datos de actividad usando expresiones regulares.
     * 
     * @param activity la actividad a validar
     * @return true si todos los datos son válidos
     */
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
