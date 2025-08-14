package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.inscripcion.RegistrationModel;
import com.hyrule.Backend.model.inscripcion.RegistrationType;
import com.hyrule.Backend.persistence.inscripcion.ControlRegistration;
import com.hyrule.interfaces.RegisterHandler;

public class InscripcionRegisterHandler implements RegisterHandler {

    // *Expresion regular */
    private static final Pattern PATRON = Pattern
            .compile(
                    "^INSCRIPCION\\s*\\([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\"\\s*,\\s*\"(EVT-\\d{8})\"\\s*,\\s*\"(ASISTENTE|CONFERENCISTA|TALLERISTA|OTRO)\"\\s*\\);$");

    private static final ControlRegistration control = new ControlRegistration();

    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {
            Matcher m = PATRON.matcher(linea.trim());
            if (!m.matches())
                return false;

            String correo = m.group(1);
            String codigoEvento = m.group(2);
            RegistrationType tipoInscripcion = RegistrationType.valueOf(m.group(3).toUpperCase());

            RegistrationModel inscripcion = new RegistrationModel(correo, codigoEvento, tipoInscripcion);

            return control.insert(inscripcion) != null;

        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando INSCRIPCION: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    public boolean isValid(RegistrationModel inscripcion) {
        try {
            if (inscripcion == null) {
                return false;
            }

            // *Validamos la integridad de los datos */
            if (!validateDataIntegrity(inscripcion)) {
                return false;
            }

            // *Insertamos la inscripcion en la base de datos */
            return control.insert(inscripcion) != null;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateDataIntegrity(RegistrationModel inscripcion) {

        return inscripcion != null && inscripcion.getCorreoParticipante() != null
                && inscripcion.getCodigoEvento() != null && inscripcion.getTipoInscripcion() != null
                && !inscripcion.getCorreoParticipante().isBlank() && !inscripcion.getCodigoEvento().isBlank()
                && Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                        inscripcion.getCorreoParticipante())
                && Pattern.matches("^EVT-\\d{8}$", inscripcion.getCodigoEvento())
                && Pattern.matches("^(ASISTENTE|CONFERENCISTA|TALLERISTA|OTRO)$",
                        inscripcion.getTipoInscripcion().name());
    }

}
