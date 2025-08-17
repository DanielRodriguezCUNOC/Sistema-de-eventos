package com.hyrule.Backend.RegularExpresion;

import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.model.participant.ParticipantType;

public class RExpresionParticipante {

    // *Expresion regular para validar el correo */
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // *Expresion regular para validar el nombre completo */
    public static final String NOMBRE_COMPLETO = "^[a-zA-ZÁÉÍÓÚáéíóúÑñ\\s]{1,45}$";

    // *Expresion regular para validar la institucion */
    public static final String INSTITUCION = "^[a-zA-ZÁÉÍÓÚáéíóúÑñ0-9.\\-]{1,150}$";

    // *Funcion para validar los datos sin importar el orden */
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

            if (valor.matches(EMAIL)) {
                correo = valor;
            } else if (valor.matches(NOMBRE_COMPLETO)) {
                nombre = valor;
            } else if (valor.matches(INSTITUCION)) {
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

    // *Metodo para convertir el tipo de evento */
    private boolean isParticipantType(String participantType) {
        for (ParticipantType type : ParticipantType.values()) {
            if (type.name().equals(participantType)) {
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
