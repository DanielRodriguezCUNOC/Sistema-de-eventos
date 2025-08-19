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

        // Verificamos que tenga el formato básico
        if (!linea.startsWith("REGISTRO_INSCRIPCION") || !linea.endsWith(");")) {
            return null;
        }

        // Eliminamos el prefijo y sufijo
        String contenido = linea.substring("REGISTRO_INSCRIPCION(".length(), linea.length() - 2).trim();

        // Dividimos respetando comillas
        String[] partes = splitArgs(contenido);

        if (partes.length != 3) {
            return null;
        }

        try {
            String correo = partes[0].replaceAll("^\"|\"$", "").trim();
            String codigoEvento = partes[1].replaceAll("^\"|\"$", "").trim();
            RegistrationType tipoInscripcion = RegistrationType.valueOf(partes[2].replaceAll("^\"|\"$", "").trim());

            if (!EMAIL.matcher(correo).matches())
                return null;
            if (!CODIGO_EVENTO.matcher(codigoEvento).matches())
                return null;

            return new RegistrationModel(correo, codigoEvento, tipoInscripcion);

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
