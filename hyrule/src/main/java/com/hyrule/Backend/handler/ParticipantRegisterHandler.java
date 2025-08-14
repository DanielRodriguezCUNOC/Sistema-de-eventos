package com.hyrule.Backend.handler;

import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.model.participant.ParticipantType;
import com.hyrule.Backend.persistence.participant.ControlParticipant;
import com.hyrule.interfaces.RegisterHandler;

public class ParticipantRegisterHandler implements RegisterHandler {

    // *Expresion regular para validar el registro de participantes */
    private static final Pattern PATRON = Pattern.compile(
            "^REGISTRO_PARTICIPANTE\\s*\\(\\s*\"[a-zA-ZÁÉÍÓÚáéíóúÑñ]{1,45})\"\\s*,\\s*\"(ESTUDIANTE|PROFESIONAL|INVITADO)\"\\s*,\\s*\"([a-zA-ZÁÉÍÓÚáéíóúÑñ0-9.\\-]{1,150})\"\\s*,\\s*\"([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\"\\s*\\);$");

    // * Creamos una instancia para ingresar los datos a la BD */
    private final ControlParticipant control = new ControlParticipant();

    @Override
    public boolean process(String linea, BufferedWriter logWriter) {
        try {
            // *Validamos la linea con la expresion regular */
            Matcher m = PATRON.matcher(linea.trim());
            if (!m.matches())
                return false;

            String correo = m.group(1);
            String nombre = m.group(2);
            String institucion = m.group(3);
            ParticipantType tipoParticipante = ParticipantType.valueOf(m.group(4));

            // *Creamos una instancia de la tabla Participante */
            ParticipantModel participante = new ParticipantModel(correo, nombre, tipoParticipante, institucion);
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

    // *Funcion para registrar un participante desde el formulario */
    public boolean isValid(ParticipantModel participante) {
        try {
            if (participante == null) {
                return false;
            }

            // *Validamos la integridad de los datos */
            if (!validateDataIntegrity(participante)) {
                return false;
            }

            // *Insertamos el participante en la base de datos */
            return control.insert(participante) != null;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // *Funcion para validar la integridad de los datos del participante */
    private boolean validateDataIntegrity(ParticipantModel participante) {
        return participante.getCorreo_participante() != null && !participante.getCorreo_participante().isEmpty()
                && participante.getNombre_completo() != null && !participante.getNombre_completo().isEmpty()
                && participante.getTipo_participante() != null
                && participante.getInstitucion() != null && !participante.getInstitucion().isEmpty()
                && Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                        participante.getCorreo_participante())
                && Pattern.matches("^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ.,:;()\\-\\s]{1,45}$",
                        participante.getNombre_completo())
                && Pattern.matches("^[a-zA-Z0-9ÁÉÍÓÚáéíóúÑñ.,:;()\\-\\s]{1,150}$",
                        participante.getInstitucion())
                && Pattern.matches("^(ESTUDIANTE|PROFESIONAL|INVITADO)$", participante.getTipo_participante().name());
    }

}
