package com.hyrule.Backend.RegularExpresion;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.activity.ActivityModel;
import com.hyrule.Backend.model.activity.ActivityType;

/**
 * Clase para parsear líneas de actividades usando expresiones regulares.
 * Extrae y valida datos de actividades desde texto en formato específico.
 */
public class RExpresionActividad {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final Pattern CODIGO_EVENTO = Pattern.compile("^EVT-\\d{8}$");
    private static final Pattern CODIGO_ACTIVIDAD = Pattern.compile("^ACT-\\d{8}$");
    private static final Pattern CORREO_PONENTE = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern TITULO_ACTIVIDAD = Pattern.compile("^[\\p{L}\\p{N}.,()\\-\\s]{1,150}$");
    private static final Pattern CUPO_MAX_PARTICIPANTES = Pattern.compile("^\\d{1,4}$");

    public ActivityModel parseActivity(String linea) {
        // Verificación básica del formato
        if (linea == null || !linea.startsWith("REGISTRO_ACTIVIDAD(") || !linea.endsWith(");")) {
            return null;
        }

        try {
            // Extraer contenido dentro de paréntesis
            String contenido = linea.substring("REGISTRO_ACTIVIDAD".length()).trim();
            contenido = contenido.substring(1, contenido.length() - 2).trim(); // Remover () y ;

            // Dividir argumentos respetando comillas
            String[] partes = splitArgs(contenido);

            if (partes.length != 8) {

                return null;
            }

            // Limpiar y validar cada campo
            String codigoActividad = cleanQuotes(partes[0]);
            String codigoEvento = cleanQuotes(partes[1]);
            String tipoStr = cleanQuotes(partes[2]);
            String titulo = cleanQuotes(partes[3]);
            String correoPonente = cleanQuotes(partes[4]);
            String horaInicioStr = cleanQuotes(partes[5]);
            String horaFinStr = cleanQuotes(partes[6]);
            String cupoStr = partes[7].trim();

            // *Validaciones*/
            if (!CODIGO_ACTIVIDAD.matcher(codigoActividad).matches()) {

                return null;
            }
            if (!CODIGO_EVENTO.matcher(codigoEvento).matches()) {

                return null;
            }
            if (!CORREO_PONENTE.matcher(correoPonente).matches()) {
                return null;
            }
            if (!TITULO_ACTIVIDAD.matcher(titulo).matches()) {
                return null;
            }
            if (!CUPO_MAX_PARTICIPANTES.matcher(cupoStr).matches()) {
                return null;
            }

            ActivityType tipo;
            try {
                tipo = ActivityType.valueOf(tipoStr);
            } catch (IllegalArgumentException e) {
                return null;
            }

            LocalTime horaInicio = LocalTime.parse(horaInicioStr, TIME_FORMAT);
            LocalTime horaFin = LocalTime.parse(horaFinStr, TIME_FORMAT);

            if (horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)) {
                return null;
            }

            int cupo = Integer.parseInt(cupoStr);
            if (cupo <= 0 || cupo > 1000) {

                return null;
            }

            return new ActivityModel(codigoActividad, codigoEvento, tipo, titulo,
                    correoPonente, horaInicio, horaFin, cupo);

        } catch (DateTimeParseException e) {

            return null;
        } catch (NumberFormatException e) {

            return null;
        } catch (Exception e) {

            return null;
        }
    }

    private String cleanQuotes(String str) {
        return str.replaceAll("^\"|\"$", "").trim();
    }

    private String[] splitArgs(String input) {
        return input.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}