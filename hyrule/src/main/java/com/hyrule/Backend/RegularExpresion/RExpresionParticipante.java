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
    public static final Pattern NOMBRE_COMPLETO = Pattern.compile("^[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s]{1,45}$");

    /** Expresión regular para validar la institución */
    public static final Pattern INSTITUCION = Pattern.compile("^[a-zA-ZÁÉÍÓÚáéíóúÑñ0-9.\\-]{1,150}$");

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

        // *Eliminamos el prefijo y sufijo */
        String contenido = linea.substring("REGISTRO_PARTICIPANTE(".length(), linea.length() - 2).trim();

        // * Dividimos la linea por comas */
        String[] partes = splitArgs(contenido);

        // *Verificamos que tengan la cantidad de datos sea la correcta */

        if (partes.length != 4) {
            return null;
        }
        String nombre = null;
        ParticipantType tipoParticipante = null;
        String institucion = null;
        String correo = null;

        // *Ciclo para identificar el tipo de dato */
        for (String parte : partes) {

            // *Eliminamos las comillas */
            String valor = parte.replaceAll("^\"|\"$", "").trim();

            if (EMAIL.matcher(valor).matches()) {
                correo = valor;
            } else if (NOMBRE_COMPLETO.matcher(valor).matches()) {
                nombre = valor;
            } else if (INSTITUCION.matcher(valor).matches()) {
                institucion = valor;
            } else if (isParticipantType(valor)) {
                tipoParticipante = ParticipantType.valueOf(valor);
            }
        }

        // *Verificamos que todos los datos hayan sido reconocidos */
        if (correo != null && nombre != null && tipoParticipante != null && institucion != null) {

            return new ParticipantModel(correo, nombre, tipoParticipante, institucion);
        }
        return null;

    }

    /**
     * Verifica si una cadena corresponde a un tipo de participante válido.
     * 
     * @param participantType la cadena a verificar
     * @return true si es un tipo válido, false si no
     */
    private boolean isParticipantType(String participantType) {
        for (ParticipantType type : ParticipantType.values()) {
            if (type.name().equals(participantType)) {
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
