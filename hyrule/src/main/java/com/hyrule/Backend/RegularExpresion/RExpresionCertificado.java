package com.hyrule.Backend.RegularExpresion;

import com.hyrule.Backend.model.certified.CertifiedModel;

public class RExpresionCertificado {

    // *Expresion regular para validar el codigo del evento */
    private static final String CODIGO_EVENTO = "^EVT-\\d{8}$";

    // *Expresion regular para validar el correo */
    private static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // *Funcion para validar el registro de certificado */
    public CertifiedModel parseCertified(String linea) {

        if (!linea.startsWith("CERTIFICADO") || !linea.endsWith(");")) {
            return null;
        }
        // *Eliminamos el prefijo y sufijo */
        String contenido = linea.substring("CERTIFICADO(".length(), linea.length() - 2).trim();

        // * Dividimos la linea por comas */
        String[] partes = splitArgs(contenido);

        // *Verificamos que tengan la cantidad de datos sea la correcta */
        if (partes.length != 2) {
            return null;
        }

        String correo = null;
        String codigoEvento = null;

        // *Ciclo para identificar el tipo de dato */
        for (String parte : partes) {
            String valor = parte.replaceAll("^\"|\"$", "").trim();

            if (CODIGO_EVENTO.matches(valor)) {
                codigoEvento = valor;
            } else if (EMAIL.matches(valor)) {
                correo = valor;
            }
        }

        // *Verificamos que todos los datos hayan sido reconocidos */
        if (correo != null && codigoEvento != null) {
            return new CertifiedModel(codigoEvento, correo);
        }

        return null;
    }

    private String[] splitArgs(String input) {
        // *Divide los argumentos por comas*/
        return input.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
