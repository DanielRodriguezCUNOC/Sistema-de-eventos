package com.hyrule.Backend.RegularExpresion;

import java.util.regex.Pattern;

import com.hyrule.Backend.model.asistencia.AttendanceModel;

/**
 * Clase para parsear líneas de asistencia usando expresiones regulares.
 * Extrae y valida datos de asistencia desde texto en formato específico.
 */
public class RExpresionAsistencia {

    /** Expresión regular para validar el código de evento */
    public static final Pattern CODIGO_EVENTO = Pattern.compile("^EVT-\\d{8}$");
    /** Expresión regular para validar el correo electrónico */
    public static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /**
     * Parsea una línea de texto para extraer datos de asistencia.
     * 
     * @param linea línea en formato ASISTENCIA(...) con datos de asistencia
     * @return AttendanceModel con los datos parseados o null si es inválida
     */
    public AttendanceModel parseAttendance(String linea) {

        // Verificamos que tenga el formato básico
        if (!linea.startsWith("ASISTENCIA") || !linea.endsWith(");")) {
            return null;
        }

        // Eliminamos el prefijo y sufijo
        String contenido = linea.substring("ASISTENCIA(".length(), linea.length() - 2).trim();

        // Dividimos respetando comillas
        String[] partes = splitArgs(contenido);

        // Debe haber exactamente 2 partes
        if (partes.length != 2) {
            return null;
        }

        try {
            // Asignación posicional de cada campo
            String codigoEvento = partes[0].replaceAll("^\"|\"$", "").trim();
            String correo = partes[1].replaceAll("^\"|\"$", "").trim();

            if (!CODIGO_EVENTO.matcher(codigoEvento).matches())
                return null;
            if (!EMAIL.matcher(correo).matches())
                return null;

            return new AttendanceModel(codigoEvento, correo);

        } catch (Exception e) {
            // Cualquier error de parseo devuelve null
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
