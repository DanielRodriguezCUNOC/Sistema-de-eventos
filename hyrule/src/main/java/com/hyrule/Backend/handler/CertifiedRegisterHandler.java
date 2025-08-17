package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import com.hyrule.Backend.RegularExpresion.RExpresionCertificado;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.certified.CertifiedModel;
import com.hyrule.Backend.persistence.certified.ControlCertified;
import com.hyrule.interfaces.RegisterHandler;

public class CertifiedRegisterHandler implements RegisterHandler {

    public static final ControlCertified control = new ControlCertified();

    // *Estructura para almacenar datos */
    ValidationArchive validator = ValidationArchive.getInstance();

    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {

            RExpresionCertificado parser = new RExpresionCertificado();
            CertifiedModel certified = parser.parseCertified(linea.trim());
            if (certified == null) {
                logWriter.write("Línea inválida o incompleta: " + linea);
                logWriter.newLine();
                return false;
            }

            // *Validamos si el certificado ya existe */
            if (validator.existsCertificado(certified.getCorreoParticipante(), certified.getCodigoEvento())) {
                logWriter.write("El certificado ya existe para el participante: " + certified.getCorreoParticipante()
                        + " en el evento: " + certified.getCodigoEvento());
                logWriter.newLine();
                return false;
            }

            // *Agregamos los datos al HashSet */
            validator.addCertificado(certified);

            // * Insertamos el certificado en la base de datos */
            return control.insert(certified) != null;

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

        boolean codigoEventoValido = certified.getCodigoEvento() != null &&
                certified.getCodigoEvento().matches("^EVT-\\d{8}$") && !certified.getCodigoEvento().isBlank();

        boolean correoValido = certified.getCorreoParticipante() != null &&
                certified.getCorreoParticipante().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
                && !certified.getCorreoParticipante().isBlank();

        return codigoEventoValido && correoValido;
    }

}
