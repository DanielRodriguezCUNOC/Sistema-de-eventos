package com.hyrule.Backend.Validations;

import java.util.regex.Pattern;

import com.hyrule.Backend.model.participant.ParticipantType;

public class ValidateParticipantRegister {

    private String correo;
    private String nombre;
    private String tipoStr;
    private String institucion;

    // *Expresiones regulares para validaciones */

    private static final Pattern EMAIL_RE = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern NAME_RE = Pattern.compile("^[^\"\\n]{1,45}$");
    private static final Pattern TYPE_RE = Pattern.compile("^(ESTUDIANTE|PROFESIONAL|INVITADO)$");
    private static final Pattern INSTITUTION_RE = Pattern.compile("^[^\"\\n]{1,150}$");

    public ValidateParticipantRegister(String correo, String nombre, String tipoStr, String institucion) {
        this.correo = correo;
        this.nombre = nombre;
        this.tipoStr = tipoStr;
        this.institucion = institucion;
    }

    public boolean isValid() {
        return validateEmptyFields() && validatePatterns() && valideParticipantType();
    }

    // *Verificamos que no hay datos vacio*/
    public boolean validateEmptyFields() {
        return !correo.isEmpty() && !nombre.isEmpty() && !tipoStr.isEmpty() && !institucion.isEmpty();
    }

    // *Validamos la integridad de los datos */
    private boolean validatePatterns() {
        return EMAIL_RE.matcher(correo).matches() &&
                NAME_RE.matcher(nombre).matches() &&
                TYPE_RE.matcher(tipoStr).matches() &&
                INSTITUTION_RE.matcher(institucion).matches();
    }

    private boolean valideParticipantType() {
        try {
            ParticipantType.valueOf(tipoStr.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

}
