package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.SQLException;

import com.hyrule.Backend.RegularExpresion.RExpresionAsistencia;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.asistencia.AttendanceModel;
import com.hyrule.Backend.persistence.asistencia.ControlAttendance;
import com.hyrule.interfaces.RegisterHandler;

/**
 * Manejador para el registro de asistencias desde archivos y formularios.
 * Procesa validaciones, duplicados y persistencia de asistencias a actividades.
 */
public class AttendanceRegisterHandler implements RegisterHandler {

    /** Controlador de persistencia para operaciones de asistencias */
    public ControlAttendance control = new ControlAttendance();

    /** Conexión a la base de datos. */
    private Connection conn;

    /** Constructor que recibe la conexión a la base de datos. */
    public AttendanceRegisterHandler(Connection conn) {
        this.conn = conn;
    }

    /** Validador singleton para verificar duplicados */
    ValidationArchive validator = ValidationArchive.getInstance();

    /**
     * Procesa una línea de asistencia desde archivo de carga masiva.
     * 
     * @param linea     línea del archivo con datos de la asistencia
     * @param logWriter escritor para registrar errores y éxitos
     * @return true si la asistencia se procesó correctamente
     */
    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {

            // *Validamos la linea con la expresion regular */
            RExpresionAsistencia parser = new RExpresionAsistencia();
            AttendanceModel attendance = parser.parseAttendance(linea.trim());
            if (attendance == null) {
                logWriter.write("Línea inválida o incompleta: " + linea);
                logWriter.newLine();
                return false;
            }

            // *Validamos si la asistencia ya existe */
            if (validator.existsAttendance(attendance.getCorreoParticipante(), attendance.getCodigoActividad())) {
                logWriter.write("La asistencia ya existe para el participante: " + attendance.getCorreoParticipante()
                        + " en la actividad: " + attendance.getCodigoActividad());
                logWriter.newLine();
                return false;
            }

            // *Agregamos los datos al HashSet */
            validator.addAsistencia(attendance);

            // * Insertamos la asistencia en la base de datos */
            return control.insert(attendance, conn) != null;

        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando VALIDAR_INSCRIPCION: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    /**
     * Inserta una asistencia directamente desde formulario.
     * 
     * @param attendance la asistencia a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertFromForm(AttendanceModel attendance) {

        // * Insertamos la asistencia en la base de datos */
        try {
            return control.insert(attendance, conn) != null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * Valida los datos de una asistencia desde formulario.
     * 
     * @param attendance la asistencia a validar
     * @return "Ok" si es válida, mensaje de error si no
     */
    public String validateForm(AttendanceModel attendance) {

        if (attendance == null) {
            return "La asistencia no puede ser nula.";
        }

        if (!validateDataIntegrity(attendance)) {
            return "Datos de asistencia inválidos.";
        }

        try {
            String validation = control.validateAttendance(attendance, conn);

            if (!"Ok".equals(validation)) {
                return validation;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Ok";

    }

    /**
     * Valida la integridad de los datos de asistencia usando expresiones regulares.
     * 
     * @param attendance la asistencia a validar
     * @return true si todos los datos son válidos
     */
    private boolean validateDataIntegrity(AttendanceModel attendance) {

        boolean correoValido = attendance.getCorreoParticipante() != null &&
                attendance.getCorreoParticipante().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

        boolean codigoValido = attendance.getCodigoActividad() != null &&
                attendance.getCodigoActividad().matches("^ACT-\\d{8}$");

        return correoValido && codigoValido;
    }

}
