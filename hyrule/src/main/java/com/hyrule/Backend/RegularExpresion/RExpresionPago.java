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

        // Verificamos que tenga el formato básico
        if (!linea.startsWith("REGISTRO_PAGO") || !linea.endsWith(");")) {
            return null;
        }

        // Eliminamos el prefijo y sufijo
        String contenido = linea.substring("REGISTRO_PAGO(".length(), linea.length() - 2).trim();

        // Dividimos respetando comillas
        String[] partes = splitArgs(contenido);

        // Debe haber exactamente 4 partes
        if (partes.length != 4) {
            return null;
        }

        try {
            // * Asignación posicional de cada campo y eliminación de comillas y espacios*/
            String correo = partes[0].replaceAll("^\"|\"$", "").trim();
            String codigoEvento = partes[1].replaceAll("^\"|\"$", "").trim();
            PaymentType tipoPago = PaymentType.valueOf(partes[2].replaceAll("^\"|\"$", "").trim());
            BigDecimal monto = new BigDecimal(partes[3].trim());

            if (!EMAIL.matcher(correo).matches())
                return null;
            if (!CODIGO_EVENTO.matcher(codigoEvento).matches())
                return null;
            if (!MONTO.matcher(partes[3].trim()).matches())
                return null;

            return new PaymentModel(correo, codigoEvento, tipoPago, monto);

        } catch (Exception e) {
            return null;
        }
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
