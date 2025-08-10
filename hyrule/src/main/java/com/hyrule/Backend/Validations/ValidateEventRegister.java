package com.hyrule.Backend.Validations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.event.EventType;

public class ValidateEventRegister {

    // *Datos que vendran del formulario */
    private String codigo;
    private String fechaStr;
    private String tipoStr;
    private String titulo;
    private String ubicacion;
    private int cupoMax;

    // *Expresiones regulares para validaciones */
    private static final Pattern CODE_RE = Pattern.compile("^EVT-\\d{3}$");
    private static final Pattern DATE_RE = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$");
    private static final Pattern TITLE_RE = Pattern.compile("^[^\"\\n]{1,150}$");
    private static final Pattern LOCATION_RE = Pattern.compile("^[^\"\\n]{1,150}$");
    private static final Pattern TYPE_RE = Pattern.compile("^(CHARLA|CONGRESO|TALLER|DEBATE)$");

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ValidateEventRegister(String codigo, String fechaStr, String tipoStr, String titulo, String ubicacion,
            int cupoMax) {
        this.codigo = codigo;
        this.fechaStr = fechaStr;
        this.tipoStr = tipoStr;
        this.titulo = titulo;
        this.ubicacion = ubicacion;
        this.cupoMax = cupoMax;

    }

    public boolean isValid() {
        return validateEmptyFields() && validatePatterns() && valideEventType() && validateDate();
    }

    // *Verificamos que no hay datos vacio */
    public boolean validateEmptyFields() {
        return !codigo.isEmpty() && !fechaStr.isEmpty() && !tipoStr.isEmpty() && !titulo.isEmpty()
                && !ubicacion.isEmpty() && cupoMax > 0;
    }

    // *Validamos la integridad de los datos */
    private boolean validatePatterns() {
        return CODE_RE.matcher(codigo).matches() &&
                DATE_RE.matcher(fechaStr).matches() &&
                TYPE_RE.matcher(tipoStr).matches() &&
                TITLE_RE.matcher(titulo).matches() &&
                LOCATION_RE.matcher(ubicacion).matches();
    }

    private boolean valideEventType() {
        try {
            EventType.valueOf(tipoStr.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean validateDate() {
        try {
            LocalDate.parse(fechaStr, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
