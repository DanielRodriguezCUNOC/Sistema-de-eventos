package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.validate_registration.ValidateRegistrationModel;
import com.hyrule.Backend.persistence.validate_registration.ControlValidateRegistration;
import com.hyrule.Backend.records.PaymentRegistry;
import com.hyrule.interfaces.RegisterHandler;

public class ValidateInscripcionRegisterHandler implements RegisterHandler {

    // *Expresion regular */

    private static final Pattern Patron = Pattern
            .compile(
                    "^VALIDAR_INSCRIPCION\\s*(\\s*\"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\"\\s*,\\s*\"(EVT-\\d{8})\"\\s*\\);$");

    public static final ControlValidateRegistration control = new ControlValidateRegistration();

    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {
            Matcher m = Patron.matcher(linea.trim());
            if (!m.matches())
                return false;
            String correo = m.group(1);
            String codigoEvento = m.group(2);

            // *Validamos que exista un pago registrado para el correo y evento */
            if (!PaymentRegistry.existePago(correo, codigoEvento)) {
                try {
                    logWriter.write(
                            "No existe pago registrado para el correo: " + correo + " y evento: " + codigoEvento);
                    logWriter.newLine();
                } catch (Exception ignore) {
                }
                return false;
            }

            ValidateRegistrationModel inscripcion = new ValidateRegistrationModel(correo, codigoEvento);

            return control.insert(inscripcion) != null;

        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando VALIDAR_INSCRIPCION: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    public boolean isValid(ValidateRegistrationModel validarInscripcion) {
        try {
            if (validarInscripcion == null) {
                return false;
            }

            // *Validamos la integridad de los datos */
            if (!validateDataIntegrity(validarInscripcion)) {
                return false;
            }

            // *Validamos que exista un pago registrado para el correo y evento */
            if (!PaymentRegistry.existePago(validarInscripcion.getCorreo(), validarInscripcion.getCodigoEvento())) {
                return false;
            }

            return control.insert(validarInscripcion) != null;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean validateDataIntegrity(ValidateRegistrationModel validarInscripcion) {
        return validarInscripcion.getCorreo() != null
                && !validarInscripcion.getCorreo().isBlank()
                && validarInscripcion.getCodigoEvento() != null
                && !validarInscripcion.getCodigoEvento().isBlank()
                && Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", validarInscripcion.getCorreo())
                && Pattern.matches("^EVT-\\d{8}$", validarInscripcion.getCodigoEvento());
    }

}
