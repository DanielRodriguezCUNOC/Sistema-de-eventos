package com.hyrule.Backend.RegularExpresion;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.payment.PaymentModel;
import com.hyrule.Backend.model.payment.PaymentType;

/**
 * Clase para parsear líneas de pagos usando expresiones regulares.
 * Extrae y valida datos de pagos desde texto en formato específico.
 */
public class RExpresionPago {

    /** Expresión regular para validar el código de evento */
    public static final Pattern CODIGO_EVENTO = Pattern.compile("^EVT-\\d{8}$");

    /** Expresión regular para validar el monto del pago */
    public static final Pattern MONTO = Pattern.compile("^(\\d+)(\\.\\d{1,2})?$");

    /** Expresión regular para validar el correo electrónico */
    public static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /**
     * Parsea una línea de texto para extraer datos de pago.
     * 
     * @param linea línea en formato REGISTRO_PAGO(...) con datos del pago
     * @return PaymentModel con los datos parseados o null si es inválida
     */
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

            if (CODIGO_EVENTO.matcher(valor).matches()) {
                codigoEvento = valor;
            } else if (EMAIL.matcher(valor).matches()) {
                correo = valor;
            } else if (MONTO.matcher(valor).matches()) {
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

    /**
     * Verifica si una cadena corresponde a un tipo de pago válido.
     * 
     * @param paymentType la cadena a verificar
     * @return true si es un tipo válido, false si no
     */
    private boolean isPaymentType(String paymentType) {
        for (PaymentType type : PaymentType.values()) {
            if (type.name().equals(paymentType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Divide los argumentos de entrada por comas respetando las comillas.
     * 
     * @param input la cadena de argumentos a dividir
     * @return array de argumentos separados
     */
    private String[] splitArgs(String input) {
        // *Divide los argumentos por comas*/
        return input.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
