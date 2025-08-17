package com.hyrule.Backend.RegularExpresion;

import java.math.BigDecimal;

import com.hyrule.Backend.model.payment.PaymentModel;
import com.hyrule.Backend.model.payment.PaymentType;

public class RExpresionPago {

    // *Expresion regular para validar el codigo de pago */
    public static final String CODIGO_EVENTO = "^EVT-\\d{8}$";

    // *Expresion regular para validar el monto */
    public static final String MONTO = "^(\\d+)(\\.\\d{1,2})?$";

    // *Expresion regular para validar el correo */
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // *Funcion para validar el tipo de inscripcion */
    public PaymentModel parsePayment(String linea) {
        if (!linea.startsWith("REGISTRO_PAGO") || !linea.endsWith(");")) {
            return null;
        }

        // *Eliminamos el prefijo y sufijo */
        String contenido = linea.substring("REGISTRO_PAGO(".length(), linea.length() - 2).trim();

        // * Dividimos la linea por comas */
        String[] partes = splitArgs(contenido);

        // *Verificamos que tengan la cantidad de datos sea la correcta */

        if (partes.length != 3) {
            return null;
        }

        String correo = null;
        String codigoEvento = null;
        BigDecimal monto = null;
        PaymentType tipoPago = null;

        // *Ciclo para identificar el tipo de dato */
        for (String parte : partes) {

            // *Eliminamos las comillas */
            String valor = parte.replaceAll("^\"|\"$", "").trim();

            if (CODIGO_EVENTO.matches(valor)) {
                codigoEvento = valor;
            } else if (EMAIL.matches(valor)) {
                correo = valor;
            } else if (MONTO.matches(valor)) {
                monto = new BigDecimal(valor);
            } else if (isPaymentType(valor)) {
                tipoPago = PaymentType.valueOf(valor);

            }

        }

        // *Verificamos que todos los datos hayan sido reconocidos */
        if (correo != null && codigoEvento != null && monto != null) {
            return new PaymentModel(correo, codigoEvento, tipoPago, monto);
        }

        return null;
    }

    // *Metodo para convertir el tipo de evento */
    private boolean isPaymentType(String paymentType) {
        for (PaymentType type : PaymentType.values()) {
            if (type.name().equals(paymentType)) {
                return true;
            }
        }
        return false;
    }

    private String[] splitArgs(String input) {
        // *Divide los argumentos por comas*/
        return input.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
