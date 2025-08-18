package com.hyrule.Backend.RegularExpresion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.event.EventModel;
import com.hyrule.Backend.model.event.EventType;

/**
 * Clase para parsear líneas de eventos usando expresiones regulares.
 * Extrae y valida datos de eventos desde texto en formato específico.
 */
public class RExpresionEvento {

    /** Formato de fecha para parseo */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Expresión regular para validar el código del evento */
    public static final Pattern CODIGO_EVENTO = Pattern.compile("^EVT-\\d{8}$");

    /** Expresión regular para validar la fecha del evento */
    public static final Pattern FECHA_EVENTO = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$");

    /** Expresión regular para validar el título del evento */
    public static final Pattern TITULO_EVENTO = Pattern.compile("^[a-zA-ZÁÉÍÓÚáéíóúÑñ0-9\\-\\s]{1,150}$");

    /** Expresión regular para validar la ubicación del evento */
    public static final Pattern UBICACION_EVENTO = Pattern.compile("^[a-zA-ZÁÉÍÓÚáéíóúÑñ0-9.,()\\-\\s]{1,150}$");

    /** Expresión regular para validar el cupo máximo de participantes */
    public static final Pattern CUPO_MAX_PARTICIPANTES = Pattern.compile("^(\\d+)$");

    /** Expresión regular para validar el costo del evento */
    public static final Pattern COSTO_EVENTO = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    /**
     * Parsea una línea de texto para extraer datos de evento.
     * 
     * @param linea línea en formato REGISTRO_EVENTO(...) con datos del evento
     * @return EventModel con los datos parseados o null si es inválida
     */
    public EventModel parseEvent(String linea) {

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

        String codigo = null;
        LocalDate fechaStr = null;
        EventType tipoStr = null;
        String titulo = null;
        String ubicacion = null;
        Integer cupoStr = null;
        BigDecimal costoStr = null;

        // *Ciclo para reconocer el tipo de dato por si viene desordenado */
        for (String parte : partes) {

            // *Eliminamos las comillas */
            String valor = parte.replaceAll("^\"|\"$", "").trim();
            if (CODIGO_EVENTO.matcher(valor).matches()) {
                codigo = valor;
            } else if (FECHA_EVENTO.matcher(valor).matches()) {
                fechaStr = LocalDate.parse(valor, DATE_FORMAT);
            } else if (isEventType(valor)) {
                tipoStr = EventType.valueOf(valor);
            } else if (TITULO_EVENTO.matcher(valor).matches()) {
                titulo = valor;
            } else if (UBICACION_EVENTO.matcher(valor).matches()) {
                ubicacion = valor;
            } else if (CUPO_MAX_PARTICIPANTES.matcher(valor).matches()) {
                cupoStr = Integer.parseInt(valor);
            } else if (COSTO_EVENTO.matcher(valor).matches()) {
                costoStr = new BigDecimal(valor);
            }
        }
        // *Verificamos que todos los datos hayan sido reconocidos */

        if (codigo != null && fechaStr != null && tipoStr != null && titulo != null && ubicacion != null
                && cupoStr != null && costoStr != null) {
            return new EventModel(codigo, fechaStr, tipoStr, titulo, ubicacion, cupoStr, costoStr);
        }
        return null;
    }

    /**
     * Verifica si una cadena corresponde a un tipo de evento válido.
     * 
     * @param eventType la cadena a verificar
     * @return true si es un tipo válido, false si no
     */
    private boolean isEventType(String eventType) {
        for (EventType type : EventType.values()) {
            if (type.name().equals(eventType)) {
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
