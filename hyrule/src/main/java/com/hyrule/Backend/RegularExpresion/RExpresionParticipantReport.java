package com.hyrule.Backend.RegularExpresion;

import java.util.regex.Pattern;

import com.hyrule.Backend.model.participant.ParticipantType;

public class RExpresionParticipantReport {

    /** Expresión regular para validar la institución */
    public static final Pattern INSTITUCION = Pattern.compile("^[\\p{L}\\p{N}.,()\\-\\s]{1,150}$");

    /** Expresión regular para validar el código del evento */
    public static final Pattern CODIGO_EVENTO = Pattern.compile("^EVT-\\d{8}$");

    /**
     * Valida si la línea del reporte cumple con el formato esperado.
     * 
     * @param linea Línea del reporte a validar.
     * @return true si la línea es válida, false en caso contrario.
     */
    public String[] parseParticipantReport(String linea) {

        String[] contenidoLinea = null;

        if (!linea.startsWith("REPORTE_PARTICIPANTES") || !linea.endsWith(");")) {
            return null;

        }

        String contenido = linea.substring("REPORTE_PARTICIPANTES(".length(), linea.length() - 2);
        String[] partes = splitArgs(contenido);

        if (partes.length != 3) {
            return null;
        }

        try {
            String codigo = partes[0].replaceAll("^\"|\"$", "").trim();
            ParticipantType tipoParticipante = ParticipantType.valueOf(partes[1].replaceAll("^\"|\"$", "").trim());
            String institucion = partes[3].replaceAll("^\"|\"$", "").trim();

            if (!CODIGO_EVENTO.matcher(codigo).matches()) {
                return null;
            }
            if (!INSTITUCION.matcher(institucion).matches()) {
                return null;
            }

            contenidoLinea = new String[3];
            contenidoLinea[0] = codigo;
            contenidoLinea[1] = tipoParticipante.toString();
            contenidoLinea[2] = institucion;

            return contenidoLinea;
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
