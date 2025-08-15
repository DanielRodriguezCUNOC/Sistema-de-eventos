package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.asistencia.AttendanceModel;
import com.hyrule.Backend.persistence.asistencia.ControlAttendance;
import com.hyrule.interfaces.RegisterHandler;

public class AttendanceRegisterHandler implements RegisterHandler {

    // *Expresion regular */
    private static final Pattern Patron = Pattern.compile(
            "^ASISTENCIA\\(\"([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\"\\s*,\\s*\"(ACT-\\d{8})\"\\);$");

    public static final ControlAttendance control = new ControlAttendance();

    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {
            Matcher m = Patron.matcher(linea.trim());
            if (!m.matches())
                return false;

            String correo = m.group(1);
            String codigoEvento = m.group(2);

            AttendanceModel attendance = new AttendanceModel(codigoEvento, correo);

            if (control.insert(attendance) != null) {
                return true;
            } else {
                logWriter.write(
                        "No se pudo registrar la asistencia para el correo: " + correo + " y evento: " + codigoEvento);
                logWriter.newLine();
                return false;
            }
        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando VALIDAR_INSCRIPCION: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    public boolean isValid(AttendanceModel attendance) {
        try {
            if (attendance == null) {
                return false;
            }

            if (!validateDataIntegrity(attendance)) {
                return false;
            }

            // *Validamos que no exista duplicado o el cupo esta lleno*/

            if (control.insert(attendance) != null)
                return true;

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean validateDataIntegrity(AttendanceModel attendance) {
        return attendance != null &&
                attendance.getCodigoActividad() != null &&
                !attendance.getCodigoActividad().isEmpty() &&
                attendance.getCorreoParticipante() != null &&
                !attendance.getCorreoParticipante().isEmpty()
                && Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                        attendance.getCorreoParticipante())
                && Pattern.matches("^ACT-\\d{8}$", attendance.getCodigoActividad());
    }

}
