package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.model.participant.ParticipantType;
import com.hyrule.Backend.persistence.participant.ControlParticipant;
import com.hyrule.interfaces.RegisterHandler;

public class ParticipantRegisterHandler implements RegisterHandler {

    // *Expresion regular para validar el registro de participantes */
    private static final Pattern PATRON = Pattern.compile(
            "^REGISTRO_PARTICIPANTE\\s*\\(\\s*\"([a-zA-ZÁÉÍÓÚáéíóúÑñ]{1,45})\"\\s*,\\s*\"(ESTUDIANTE|PROFESIONAL|INVITADO)\"\\s*,\\s*\"([a-zA-ZÁÉÍÓÚáéíóúÑñ0-9.\\-]{1,150})\"\\s*,\\s*\"([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\"\\s*\\);$");

    // * Creamos una instancia para ingresar los datos a la BD */
    private final ControlParticipant control = new ControlParticipant();

    // *Estructura para el almacenamiento de datos */
    private final Set<String> archiveEmails = new HashSet<>();
    private final Set<String> archiveNames = new HashSet<>();

    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {

            // *Validamos la linea con la expresion regular */
            Matcher m = PATRON.matcher(linea.trim());
            if (!m.matches())
                return false;

            String nombre = m.group(1);
            ParticipantType tipoParticipante = ParticipantType.valueOf(m.group(2));
            String institucion = m.group(3);
            String correo = m.group(4);

            // *Creamos una instancia de la tabla Participante */
            ParticipantModel participante = new ParticipantModel(correo, nombre, tipoParticipante, institucion);

            // *Verificamos duplicados */
            if (archiveEmails.contains(correo)) {
                logWriter.write("Correo de participante duplicado en el archivo: " + correo);
                logWriter.newLine();
                return false;

            }
            if (archiveNames.contains(nombre)) {
                logWriter.write("Nombre de participante duplicado en el archivo: " + nombre);
                logWriter.newLine();
                return false;
            }

            // *Agregamos a los archivos internos */
            archiveEmails.add(correo);
            archiveNames.add(nombre);

            logWriter.write("Participante registrado: " + participante);
            logWriter.newLine();

            // * Insertamos el participante en la base de datos */
            return control.insert(participante) != null;
        } catch (Exception e) {
            try {
                logWriter.write("Excepción procesando REGISTRO_PARTICIPANTE: " + e.getMessage());
                logWriter.newLine();
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    public boolean insertFromForm(ParticipantModel participant) {
        return control.insert(participant) != null;
    }

    // *Funcion para registrar un participante desde el formulario */
    public String validateForm(ParticipantModel participante) {

        if (participante == null) {
            return "Participante no puede ser nulo";
        }

        // *Validamos la integridad de los datos */
        if (!validateDataIntegrity(participante)) {
            return "Datos del participante no son válidos";
        }

        // *Validamos que no existan participantes con el mismo correo o nombre */
        String validation = control.validateParticipant(participante);
        if (!"Ok".equals(validation)) {
            return validation;
        }

        return "Ok";
    }

    // *Funcion para validar la integridad de los datos del participante */
    private boolean validateDataIntegrity(ParticipantModel participante) {

        boolean isValidEmail = Pattern.matches(
                "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", participante.getCorreo_participante())
                && participante.getCorreo_participante() != null && !participante.getCorreo_participante().isBlank();

        boolean isValidName = Pattern.matches(
                "^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ\\-\\s]{1,45}$", participante.getNombre_completo())
                && participante.getNombre_completo() != null && !participante.getNombre_completo().isBlank();

        boolean isValidInstitution = Pattern.matches(
                "^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ.,:;()\\-\\s]{1,150}$", participante.getInstitucion())
                && participante.getInstitucion() != null && !participante.getInstitucion().isBlank();

        boolean isValidType = Pattern.matches(
                "^(ESTUDIANTE|PROFESIONAL|INVITADO)$", participante.getTipo_participante().name())
                && participante.getTipo_participante() != null;

        return isValidEmail && isValidName && isValidInstitution && isValidType;
    }

}
