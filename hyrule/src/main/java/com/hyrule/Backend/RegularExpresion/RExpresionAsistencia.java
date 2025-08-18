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

        if (!linea.startsWith("ASISTENCIA") || !linea.endsWith(");")) {
            return null;
        }

        // *Eliminamos el prefijo y sufijo */
        String contenido = linea.substring("ASISTENCIA(".length(), linea.length() - 2).trim();

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

            if (CODIGO_EVENTO.matcher(valor).matches()) {
                codigoEvento = valor;
            } else if (EMAIL.matcher(valor).matches()) {
                correo = valor;
            }
        }

        // *Verificamos que todos los datos hayan sido reconocidos */
        if (correo != null && codigoEvento != null) {
            return new AttendanceModel(codigoEvento, correo);
        }

        return null;
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
