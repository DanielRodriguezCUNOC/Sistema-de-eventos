package com.hyrule.Backend.RegularExpresion;

import com.hyrule.Backend.model.inscripcion.RegistrationModel;
import com.hyrule.Backend.model.inscripcion.RegistrationType;

public class RExpresionInscripcion {

    // *Expresion regular para validar el correo */
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // *Expresion regular para validar el codigo de evento */
    public static final String CODIGO_EVENTO = "^EVT-\\d{8}$";

    // *Funcion para validar el tipo de inscripcion */
    public RegistrationModel parseRegistration(String linea) {
        if (!linea.startsWith("REGISTRO_INSCRIPCION") || !linea.endsWith(");")) {
            return null;
        }

        // *Eliminamos el prefijo y sufijo */
        String contenido = linea.substring("REGISTRO_INSCRIPCION(".length(), linea.length() - 2).trim();

        // * Dividimos la linea por comas */
        String[] partes = splitArgs(contenido);

        // *Verificamos que tengan la cantidad de datos sea la correcta */
        if (partes.length != 2) {
            return null;
        }

        String correo = null;
        String codigoEvento = null;
        RegistrationType tipoInscripcion = null;

        // *Ciclo para identificar el tipo de dato */
        for (String parte : partes) {
            String valor = parte.replaceAll("^\"|\"$", "").trim();

            if (valor.matches(EMAIL)) {
                correo = valor;
            } else if (valor.matches(CODIGO_EVENTO)) {
                codigoEvento = valor;
            } else if (isRegistrationType(codigoEvento)) {
                tipoInscripcion = RegistrationType.valueOf(codigoEvento);

            }
        }

        // *Verificamos que todos los datos hayan sido reconocidos */
        if (correo != null && codigoEvento != null && tipoInscripcion != null) {
            return new RegistrationModel(correo, codigoEvento, tipoInscripcion);
        }

        return null;
    }

    // *Metodo para convertir el tipo de inscripcion */
    private boolean isRegistrationType(String registrationType) {
        for (RegistrationType type : RegistrationType.values()) {
            if (type.name().equals(registrationType)) {
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
