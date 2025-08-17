package com.hyrule.Backend.RegularExpresion;

import com.hyrule.Backend.model.validate_registration.ValidateRegistrationModel;

public class RExpresionValidarInscripcion {

    // *Expresion regular para validar el correo */
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // *Expresion regular para validar el codigo de evento */
    public static final String CODIGO_EVENTO = "^EVT-\\d{8}$";

    // *Funcion para validar los datos de inscripcion */

    public ValidateRegistrationModel parseValidateRegistration(String linea) {
        if (!linea.startsWith("VALIDAR_INSCRIPCION") || !linea.endsWith(");")) {
            return null;
        }

        // *Eliminamos el prefijo y sufijo */
        String contenido = linea.substring("VALIDAR_INSCRIPCION(".length(), linea.length() - 2).trim();

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

            // *Eliminamos las comillas */
            String valor = parte.replaceAll("^\"|\"$", "").trim();

            if (CODIGO_EVENTO.matches(valor)) {
                codigoEvento = valor;
            } else if (EMAIL.matches(valor)) {
                correo = valor;

            }
        }

        // *Verificamos que todos los datos hayan sido reconocidos */
        if (correo != null && codigoEvento != null) {
            return new ValidateRegistrationModel(correo, codigoEvento);
        }

        return null;
    }

    private String[] splitArgs(String input) {
        // *Divide los argumentos por comas*/
        return input.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
