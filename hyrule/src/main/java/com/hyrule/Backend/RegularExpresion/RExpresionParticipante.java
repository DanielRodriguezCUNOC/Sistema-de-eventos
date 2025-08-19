package com.hyrule.Backend.RegularExpresion;

import java.util.regex.Pattern;

import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.model.participant.ParticipantType;

/**
 * Clase para parsear líneas de participantes usando expresiones regulares.
 * Extrae y valida datos de participantes desde texto en formato específico.
 */
public class RExpresionParticipante {

    /** Expresión regular para validar el correo electrónico */
    public static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /** Expresión regular para validar el nombre completo */
    public static final Pattern NOMBRE_COMPLETO = Pattern.compile("^[\\p{L}\\p{N}.,()\\-\\s]{1,45}$");

    /** Expresión regular para validar la institución */
    public static final Pattern INSTITUCION = Pattern.compile("^[\\p{L}\\p{N}.,()\\-\\s]{1,150}$");

    /**
     * Parsea una línea de texto para extraer datos de participante.
     * 
     * @param linea línea en formato REGISTRO_PARTICIPANTE(...) con datos del
     *              participante
     * @return ParticipantModel con los datos parseados o null si es inválida
     */
    public ParticipantModel parseParticipant(String linea) {
        if (!linea.startsWith("REGISTRO_PARTICIPANTE") || !linea.endsWith(");")) {
            return null;
        }

        String contenido = linea.substring("REGISTRO_PARTICIPANTE(".length(), linea.length() - 2).trim();
        String[] partes = splitArgs(contenido);

        if (partes.length != 4) {
            return null;
        }

        try {
            String nombre = partes[0].replaceAll("^\"|\"$", "").trim();
            ParticipantType tipoParticipante = ParticipantType.valueOf(partes[1].replaceAll("^\"|\"$", "").trim());
            String institucion = partes[2].replaceAll("^\"|\"$", "").trim();
            String correo = partes[3].replaceAll("^\"|\"$", "").trim();

            if (!EMAIL.matcher(correo).matches())
                return null;
            if (!NOMBRE_COMPLETO.matcher(nombre).matches())
                return null;
            if (!INSTITUCION.matcher(institucion).matches())
                return null;

            return new ParticipantModel(correo, nombre, tipoParticipante, institucion);

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
