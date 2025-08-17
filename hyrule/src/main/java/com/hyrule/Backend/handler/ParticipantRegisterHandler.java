package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.util.regex.Pattern;

import com.hyrule.Backend.RegularExpresion.RExpresionParticipante;
import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.persistence.participant.ControlParticipant;
import com.hyrule.interfaces.RegisterHandler;

public class ParticipantRegisterHandler implements RegisterHandler {

    // * Creamos una instancia para ingresar los datos a la BD */
    private final ControlParticipant control = new ControlParticipant();

    // *Estructura para el almacenamiento de datos */
    ValidationArchive validator = ValidationArchive.getInstance();

    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {

            // *Validamos con la expresion regular */
            RExpresionParticipante parser = new RExpresionParticipante();
            ParticipantModel participante = parser.parseParticipant(linea.trim());
            if (participante == null) {
                logWriter.write("Línea inválida o incompleta: " + linea);
                logWriter.newLine();
                return false;
            }

            // *Validamos si el participante ya existe */
            if (validator.existsParticipant(participante.getCorreo_participante())) {
                logWriter.write("El participante ya existe: " + participante.getCorreo_participante());
                logWriter.newLine();
                return false;

            }

            // *Agregamos los datos al HashSet */
            validator.addParticipante(participante);

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
                "^(ESTUDIANTE|PROFESIONAL|INVITADO)$", participante.getTipoParticipante().name())
                && participante.getTipoParticipante() != null;

        return isValidEmail && isValidName && isValidInstitution && isValidType;
    }

}
