package com.hyrule.Backend.RegularExpresion;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.activity.ActivityModel;
import com.hyrule.Backend.model.activity.ActivityType;

/**
 * Clase para parsear líneas de actividades usando expresiones regulares.
 * Extrae y valida datos de actividades desde texto en formato específico.
 */
public class RExpresionActividad {

    /** Formato de hora para parseo */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /** Expresión regular para validar el código del evento */
    private static final Pattern CODIGO_EVENTO = Pattern.compile("^EVT-\\d{8}$");

    /** Expresión regular para validar el código de la actividad */
    private static final Pattern CODIGO_ACTIVIDAD = Pattern.compile("^ACT-\\d{8}$");

    /** Expresión regular para validar el correo del ponente */
    private static final Pattern CORREO_PONENTE = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /** Expresión regular para validar el título del evento */
    private static final Pattern TITULO_ACTIVIDAD = Pattern.compile("^[\\p{L}\\p{N}.,()\\-\\s]{1,150}$");

    /** Expresión regular para validar el cupo máximo de participantes */
    private static final Pattern CUPO_MAX_PARTICIPANTES = Pattern.compile("^(\\d+)$");

    /** Expresión regular para validar la hora de inicio */
    private static final Pattern HORA_INICIO = Pattern.compile("^([0-1]\\d|2[0-3]):[0-5]\\d$");

    /** Expresión regular para validar la hora de fin */
    private static final Pattern HORA_FIN = Pattern.compile("^([0-1]\\d|2[0-3]):[0-5]\\d$");

    /**
     * Parsea una línea de texto para extraer datos de actividad.
     * 
     * @param linea línea en formato REGISTRO_EVENTO(...) con datos de actividad
     * @return ActivityModel con los datos parseados o null si es inválida
     */
    public ActivityModel parseActivity(String linea) {

        // Verificamos que tenga el formato básico
        if (!linea.startsWith("REGISTRO_ACTIVIDAD") || !linea.endsWith(");")) {
            return null;
        }

        // Eliminamos el prefijo y sufijo
        String contenido = linea.substring("REGISTRO_ACTIVIDAD(".length(), linea.length() - 2).trim();

        // Dividimos respetando comillas
        String[] partes = splitArgs(contenido);

        // Debe haber exactamente 8 partes
        if (partes.length != 8) {
            return null;
        }

        try {
            // Asignación posicional de cada campo
            String codigoEvento = partes[0].replaceAll("^\"|\"$", "").trim();
            String codigoActividad = partes[1].replaceAll("^\"|\"$", "").trim();
            ActivityType tipo = ActivityType.valueOf(partes[2].replaceAll("^\"|\"$", "").trim());
            String titulo = partes[3].replaceAll("^\"|\"$", "").trim();
            String correoPonente = partes[4].replaceAll("^\"|\"$", "").trim();
            LocalTime horaInicio = LocalTime.parse(partes[5].replaceAll("^\"|\"$", "").trim(), TIME_FORMAT);
            LocalTime horaFin = LocalTime.parse(partes[6].replaceAll("^\"|\"$", "").trim(), TIME_FORMAT);
            Integer cupo = Integer.parseInt(partes[7].trim());

            // Validaciones básicas con regex (opcional, pero recomendable)
            if (!CODIGO_EVENTO.matcher(codigoEvento).matches())
                return null;
            if (!CODIGO_ACTIVIDAD.matcher(codigoActividad).matches())
                return null;
            if (!CORREO_PONENTE.matcher(correoPonente).matches())
                return null;
            if (!TITULO_ACTIVIDAD.matcher(titulo).matches())
                return null;
            if (!HORA_INICIO.matcher(partes[5].replaceAll("^\"|\"$", "").trim()).matches())
                return null;
            if (!HORA_FIN.matcher(partes[6].replaceAll("^\"|\"$", "").trim()).matches())
                return null;
            if (!CUPO_MAX_PARTICIPANTES.matcher(partes[7].trim()).matches())
                return null;

            // Creamos y devolvemos la actividad
            return new ActivityModel(codigoActividad, codigoEvento, tipo, titulo, correoPonente, horaInicio, horaFin,
                    cupo);

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
