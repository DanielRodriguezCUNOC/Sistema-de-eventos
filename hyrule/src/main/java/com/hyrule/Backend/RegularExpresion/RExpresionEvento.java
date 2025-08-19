package com.hyrule.Backend.RegularExpresion;

import java.math.BigDecimal;
import java.text.Normalizer;
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
    public static final Pattern TITULO_EVENTO = Pattern.compile("^[\\p{L}\\p{N}\\-\\s]{1,100}$");

    /** Expresión regular para validar la ubicación del evento */
    public static final Pattern UBICACION_EVENTO = Pattern.compile("^[\\p{L}\\p{N}.,()\\-\\s]{1,150}$");

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

        String contenido = linea.substring("REGISTRO_EVENTO(".length(), linea.length() - 2).trim();
        contenido = Normalizer.normalize(contenido, Normalizer.Form.NFC);

        String[] partes = splitArgs(contenido);

        if (partes.length != 7) {
            return null;
        }

        try {

            String codigo = partes[0].replaceAll("^\"|\"$", "").trim();
            LocalDate fecha = LocalDate.parse(partes[1].replaceAll("^\"|\"$", "").trim(), DATE_FORMAT);
            EventType tipo = EventType.valueOf(partes[2].replaceAll("^\"|\"$", "").trim());
            String titulo = partes[3].replaceAll("^\"|\"$", "").trim();
            String ubicacion = partes[4].replaceAll("^\"|\"$", "").trim();
            Integer cupo = Integer.parseInt(partes[5].trim());
            BigDecimal costo = new BigDecimal(partes[6].trim());

            if (!CODIGO_EVENTO.matcher(codigo).matches())
                return null;
            if (!FECHA_EVENTO.matcher(partes[1].replaceAll("^\"|\"$", "").trim()).matches())
                return null;
            if (!TITULO_EVENTO.matcher(titulo).matches())
                return null;
            if (!UBICACION_EVENTO.matcher(ubicacion).matches())
                return null;
            if (!CUPO_MAX_PARTICIPANTES.matcher(partes[5].trim()).matches())
                return null;
            if (!COSTO_EVENTO.matcher(partes[6].trim()).matches())
                return null;

            return new EventModel(codigo, fecha, tipo, titulo, ubicacion, cupo, costo);

        } catch (Exception e) {

            return null;
        }
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
