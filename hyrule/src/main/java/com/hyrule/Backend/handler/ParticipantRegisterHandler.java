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
            "^REGISTRO_PARTICIPANTE\\s*\\(\\s*\"[^\"\\n]{1,45})\"\\s*,\\s*\"(ESTUDIANTE|PROFESIONAL|INVITADO)\"\\s*,\\s*\"([^\"\\n]{1,150})\"\\s*,\\s*\"([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\"\\s*\\);$");

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

    public boolean registerParticipantFromForm(String nombre, String correo, String institucion,
            String tipoParticipanteStr) {

        try {
            ParticipantType tipoParticipante = ParticipantType.valueOf(tipoParticipanteStr.toUpperCase());

            // *Creamos instancia de tipo Participante */

            ParticipantModel participant = new ParticipantModel(tipoParticipanteStr, nombre, tipoParticipante,
                    institucion);

            // *Insertar los datos en la BD */
            return control.insert(participant) != null;
        } catch (Exception e) {
            return false;
        }

    }

}
