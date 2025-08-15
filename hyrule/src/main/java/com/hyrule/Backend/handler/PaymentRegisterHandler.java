package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.payment.PaymentModel;
import com.hyrule.Backend.model.payment.PaymentType;
import com.hyrule.Backend.persistence.payment.ControlPayment;
import com.hyrule.interfaces.RegisterHandler;

public class PaymentRegisterHandler implements RegisterHandler {

    // *Expresion regular */

    private static final Pattern Patron = Pattern
            .compile(
                    "^PAGO\\s*(\\s*\"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\"\\s*,\\s*\"(EVT-\\d{8})\"\\s*,\\s*\"(EFECTIVO|TRANSFERENCIA|TARJETA)\"\\s*,\\s*(\\d{1,8}(\\.\\d{2})?)\\s*\\);$");

    private static final ControlPayment control = new ControlPayment();

    @Override
    public boolean process(String linea, BufferedWriter logWriter) {

        try {
            Matcher m = Patron.matcher(linea.trim());
            if (!m.matches())
                return false;

            String correo = m.group(1);
            String codigoEvento = m.group(2);
            PaymentType tipoPago = PaymentType.valueOf(m.group(3).toUpperCase());
            BigDecimal monto = new BigDecimal(m.group(4));

            PaymentModel pago = new PaymentModel(codigoEvento, correo, tipoPago, monto);

            return control.insert(pago) != null;

        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando PAGO: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    public boolean isValid(PaymentModel pago) {
        try {
            if (pago == null) {
                return false;
            }

            // *Validamos la integridad de los datos */
            if (!validateDataIntegrity(pago)) {
                return false;
            }

            // *Insertamos el pago en la base de datos */
            return control.insert(pago) != null;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validateDataIntegrity(PaymentModel payment) {

        return payment != null && payment.getCorreo() != null
                && payment.getCodigoEvento() != null && payment.getTipoPago() != null
                && payment.getMonto() != null && !payment.getCorreo().isBlank()
                && !payment.getCodigoEvento().isBlank() && payment.getMonto().compareTo(BigDecimal.ZERO) > 0
                && Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", payment.getCorreo())
                && Pattern.matches("^EVT-\\d{8}$", payment.getCodigoEvento())
                && Pattern.matches("^(EFECTIVO|TRANSFERENCIA|TARJETA)$",
                        payment.getTipoPago().name());
    }

}
