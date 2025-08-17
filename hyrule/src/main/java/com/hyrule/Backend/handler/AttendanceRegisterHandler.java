package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.sql.SQLException;

import com.hyrule.Backend.RegularExpresion.RExpresionAsistencia;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.asistencia.AttendanceModel;
import com.hyrule.Backend.persistence.asistencia.ControlAttendance;
import com.hyrule.interfaces.RegisterHandler;

public class AttendanceRegisterHandler implements RegisterHandler {

    public static final ControlAttendance control = new ControlAttendance();

    // *Estructura para validacion de asistencia */
    ValidationArchive validator = ValidationArchive.getInstance();

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
            return control.insert(attendance) != null;

        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando VALIDAR_INSCRIPCION: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    public boolean insertFromForm(AttendanceModel attendance) {

        // * Insertamos la asistencia en la base de datos */
        try {
            return control.insert(attendance) != null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public String validateForm(AttendanceModel attendance) {

        if (attendance == null) {
            return "La asistencia no puede ser nula.";
        }

        if (!validateDataIntegrity(attendance)) {
            return "Datos de asistencia inválidos.";
        }

        try {
            String validation = control.validateAttendance(attendance);

            if (!"Ok".equals(validation)) {
                return validation;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Ok";

    }

    private boolean validateDataIntegrity(AttendanceModel attendance) {

        boolean correoValido = attendance.getCorreoParticipante() != null &&
                attendance.getCorreoParticipante().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

        boolean codigoValido = attendance.getCodigoActividad() != null &&
                attendance.getCodigoActividad().matches("^ACT-\\d{8}$");

        return correoValido && codigoValido;
    }

}
