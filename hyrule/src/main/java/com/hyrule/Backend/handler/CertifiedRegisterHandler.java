package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.certified.CertifiedModel;
import com.hyrule.Backend.persistence.certified.ControlCertified;
import com.hyrule.interfaces.RegisterHandler;

public class CertifiedRegisterHandler implements RegisterHandler {
    // *Expresion regular */
    private static final Pattern Patron = Pattern.compile(
            "^CERTIFICADO\\s*\\(\\s*\"([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\"\\s*,\\s*\"(EVT-\\d{8})\"\\s*\\);$");

    public static final ControlCertified control = new ControlCertified();

    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {
            Matcher m = Patron.matcher(linea.trim());
            if (!m.matches())
                return false;

            String correo = m.group(1);
            String codigoEvento = m.group(2);

            CertifiedModel certified = new CertifiedModel(codigoEvento, correo);

            if (control.insert(certified) != null) {
                return true;
            } else {
                logWriter.write(
                        "No se pudo registrar el certificado para el correo: " + correo + " y evento: " + codigoEvento);
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

    public boolean isValid(CertifiedModel certified) {
        try {
            if (certified == null) {
                return false;
            }

            if (!validateDataIntegrity(certified)) {
                return false;
            }

            // *Validamos que no exista duplicado o el cupo esta lleno*/

            if (control.insert(certified) != null)
                return true;

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean validateDataIntegrity(CertifiedModel certified) {
        return certified != null &&
                certified.getCodigoEvento() != null &&
                !certified.getCodigoEvento().isBlank() &&
                certified.getCorreoParticipante() != null &&
                !certified.getCorreoParticipante().isBlank()
                && Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                        certified.getCorreoParticipante())
                && Pattern.matches("^EVT-\\d{8}$", certified.getCodigoEvento());
    }

}
