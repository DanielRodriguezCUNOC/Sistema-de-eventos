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
    private static final Pattern TITULO_ACTIVIDAD = Pattern.compile("^[a-zA-ZÁÉÍÓÚáéíóúÑñ0-9\\-\\s]{1,150}$");

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
    public ActivityModel parseEvent(String linea) {

        if (!linea.startsWith("REGISTRO_EVENTO") || !linea.endsWith(");")) {
            return null;

        }

        // *Eliminamos el prefijo y sufijo */
        String contenido = linea.substring("REGISTRO_EVENTO(".length(), linea.length() - 2).trim();

        // * Dividimos la linea por comas */
        String[] partes = splitArgs(contenido);

        // *Verificamos que tengan la cantidad de datos sea la correcta */

        if (partes.length != 7) {
            return null;
        }

        String codigoEvento = null;
        String codigoActividad = null;
        String correoPonente = null;
        ActivityType tipoStr = null;
        String titulo = null;
        LocalTime horaInicio = null;
        LocalTime horaFin = null;
        Integer cupoStr = null;

        // *Ciclo para reconocer el tipo de dato por si viene desordenado */
        for (String parte : partes) {

            // *Eliminamos las comillas */
            String valor = parte.replaceAll("^\"|\"$", "").trim();
            if (CODIGO_EVENTO.matcher(valor).matches()) {
                codigoEvento = valor;
            } else if (CORREO_PONENTE.matcher(valor).matches()) {
                correoPonente = valor;
            } else if (isActivityType(valor)) {
                tipoStr = ActivityType.valueOf(valor);
            } else if (TITULO_ACTIVIDAD.matcher(valor).matches()) {
                titulo = valor;
            } else if (HORA_INICIO.matcher(valor).matches()) {
                horaInicio = LocalTime.parse(valor, TIME_FORMAT);
            } else if (HORA_FIN.matcher(valor).matches()) {
                horaFin = LocalTime.parse(valor, TIME_FORMAT);
            } else if (CUPO_MAX_PARTICIPANTES.matcher(valor).matches()) {
                cupoStr = Integer.parseInt(valor);
            } else if (CODIGO_ACTIVIDAD.matcher(valor).matches()) {
                codigoActividad = valor;
            }
        }
        // *Verificamos que todos los datos hayan sido reconocidos */

        if (codigoEvento != null && correoPonente != null && tipoStr != null && titulo != null && horaInicio != null
                && horaFin != null && cupoStr != null) {

            // *Creamos una instancia de la tabla Actividad */
            return new ActivityModel(codigoActividad, codigoEvento, tipoStr, titulo, correoPonente, horaInicio, horaFin,
                    cupoStr);
        }
        return null;
    }

    /**
     * Verifica si una cadena corresponde a un tipo de actividad válido.
     * 
     * @param activityType la cadena a verificar
     * @return true si es un tipo válido, false si no
     */
    private boolean isActivityType(String activityType) {
        for (ActivityType type : ActivityType.values()) {
            if (type.name().equals(activityType)) {
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
