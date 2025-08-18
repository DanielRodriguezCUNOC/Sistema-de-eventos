package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.hyrule.Backend.RegularExpresion.RExpresionPago;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.payment.PaymentModel;
import com.hyrule.Backend.persistence.payment.ControlPayment;
import com.hyrule.interfaces.RegisterHandler;

/**
 * Manejador para el registro de pagos desde archivos y formularios.
 * Procesa validaciones, duplicados y persistencia de pagos.
 */
public class PaymentRegisterHandler implements RegisterHandler {

    /** Controlador de persistencia para operaciones de pagos */
    private ControlPayment control = new ControlPayment();

    /** Conexión a la base de datos. */
    private Connection conn;

    /** Constructor que recibe la conexión a la base de datos. */
    public PaymentRegisterHandler(Connection conn) {
        this.conn = conn;
    }

    /** Validador singleton para verificar duplicados y eventos */
    ValidationArchive validator = ValidationArchive.getInstance();

    /**
     * Procesa una línea de pago desde archivo de carga masiva.
     * 
     * @param linea     línea del archivo con datos del pago
     * @param logWriter escritor para registrar errores y éxitos
     * @return true si el pago se procesó correctamente
     */
    @Override
    public boolean process(String linea, BufferedWriter logWriter) {

        try {

            // *Validamos la linea con la expresion regular */
            RExpresionPago parser = new RExpresionPago();
            PaymentModel pago = parser.parsePayment(linea.trim());

            if (pago == null) {
                logWriter.write("Línea inválida o incompleta: " + linea);
                logWriter.newLine();
                return false;
            }

            if (!validator.existsEvent(pago.getCodigoEvento())) {
                logWriter.write("El evento no existe: " + pago.getCodigoEvento());
                logWriter.newLine();
                return false;

            }

            if (validator.existsPayment(pago.getCorreo(), pago.getCodigoEvento())) {
                logWriter.write("El participante " + pago.getCorreo() + " ya realizó el pago para el evento: "
                        + pago.getCodigoEvento());
                logWriter.newLine();
                return false;

            }

            // *Agregamos el pago a la estructura de validacion */
            validator.addPago(pago);

            logWriter.write("Pago registrado: " + pago);
            logWriter.newLine();

            // Insertamos el pago en la base de datos */
            return control.insert(pago, conn) != null;

        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando PAGO: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    /**
     * Inserta un pago directamente desde formulario.
     * 
     * @param pago el pago a insertar
     * @return true si se insertó correctamente
     */
    public boolean insertFromForm(PaymentModel pago) {
        try {
            return control.insert(pago, conn) != null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Valida los datos de un pago desde formulario.
     * 
     * @param pago el pago a validar
     * @return "Ok" si es válido, mensaje de error si no
     */
    public String validateForm(PaymentModel pago) {
        try {
            if (pago == null) {
                return "El pago no puede ser nulo.";
            }

            // *Validamos la integridad de los datos */
            if (!validateDataIntegrity(pago)) {
                return "El pago no es válido.";
            }

            String validation = control.validatePayment(pago, conn);
            if (!"Ok".equals(validation)) {
                return validation;
            }
            return "Ok";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Valida la integridad de los datos del pago usando expresiones regulares.
     * 
     * @param payment el pago a validar
     * @return true si todos los datos son válidos
     */
    private boolean validateDataIntegrity(PaymentModel payment) {

        boolean codigoValido = payment.getCodigoEvento() != null &&
                payment.getCodigoEvento().matches("^EVT-\\d{8}$") && !payment.getCodigoEvento().isBlank();

        boolean tipoPagoValido = payment.getTipoPago() != null
                && Pattern.matches("^(EFECTIVO|TARJETA|TRANSFERENCIA)$", payment.getTipoPago().name())
                && !payment.getTipoPago().name().isBlank();

        boolean montoValido = payment.getMonto() != null && payment.getMonto().compareTo(BigDecimal.ZERO) >= 0;

        boolean correoValido = payment.getCorreo() != null &&
                payment.getCorreo().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
                && !payment.getCorreo().isBlank();

        return codigoValido && tipoPagoValido && montoValido && correoValido;
    }

}
