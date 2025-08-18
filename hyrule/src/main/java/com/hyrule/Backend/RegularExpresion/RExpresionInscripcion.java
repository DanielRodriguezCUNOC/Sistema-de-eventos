package com.hyrule.Backend.RegularExpresion;

import java.util.regex.Pattern;

import com.hyrule.Backend.model.inscripcion.RegistrationModel;
import com.hyrule.Backend.model.inscripcion.RegistrationType;

/**
 * Clase para parsear líneas de inscripciones usando expresiones regulares.
 * Extrae y valida datos de inscripciones desde texto en formato específico.
 */
public class RExpresionInscripcion {

    /** Expresión regular para validar el correo electrónico */
    public static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /** Expresión regular para validar el código de evento */
    public static final Pattern CODIGO_EVENTO = Pattern.compile("^EVT-\\d{8}$");

    /**
     * Parsea una línea de texto para extraer datos de inscripción.
     * 
     * @param linea línea en formato REGISTRO_INSCRIPCION(...) con datos de
     *              inscripción
     * @return RegistrationModel con los datos parseados o null si es inválida
     */
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

            if (EMAIL.matcher(valor).matches()) {
                correo = valor;
            } else if (CODIGO_EVENTO.matcher(valor).matches()) {
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

    /**
     * Verifica si una cadena corresponde a un tipo de inscripción válido.
     * 
     * @param registrationType la cadena a verificar
     * @return true si es un tipo válido, false si no
     */
    private boolean isRegistrationType(String registrationType) {
        for (RegistrationType type : RegistrationType.values()) {
            if (type.name().equals(registrationType)) {
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
