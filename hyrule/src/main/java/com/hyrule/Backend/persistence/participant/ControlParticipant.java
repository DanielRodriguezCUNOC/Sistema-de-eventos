package com.hyrule.Backend.persistence.participant;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.persistence.Control;

public class ControlParticipant extends Control<ParticipantModel> {

    @Override
    public ParticipantModel insert(ParticipantModel entity) {

        // *Generamos la query*/
        String query = "INSERT INTO participante (correo_participante, nombre_completo, tipo_participante, institucion) VALUES (?, ?, ?, ?)";

        try (Connection conn = new DBConnection().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, entity.getCorreo_participante());
            pstmt.setString(2, entity.getNombre_completo());
            pstmt.setString(3, entity.getTipoParticipante().name());
            pstmt.setString(4, entity.getInstitucion());

            int rowsAffected = pstmt.executeUpdate();

            return (rowsAffected > 0) ? entity : null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void update(ParticipantModel entity) {
    }

    @Override
    public void delete(String key) {
    }

    @Override
    public ParticipantModel findByKey(String key) {
        return null;
    }

    @Override
    public List<ParticipantModel> findAll() throws SQLException {

        List<ParticipantModel> participants = new ArrayList<>();

        String query = "SELECT * FROM participante";

        try (Connection conn = new DBConnection().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String correo = rs.getString("correo_participante");
                String nombre = rs.getString("nombre_completo");
                String tipoParticipante = rs.getString("tipo_participante");
                String institucion = rs.getString("institucion");

                ParticipantModel participant = new ParticipantModel(correo, nombre, tipoParticipante, institucion);
                participants.add(participant);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return participants;
    }

    // *Metodo para validaciones */

    public String validateParticipant(ParticipantModel participant) {

        if (emailExists(participant.getCorreo_participante())) {
            return "Hay un participante registrado con el mismo correo electrónico.";
        }
        if (nameExists(participant.getNombre_completo())) {
            return "Hay un participante registrado con el mismo nombre.";
        }
        return "Ok";

    }

    // *Verificamos que no hay correos duplicados */
    public boolean emailExists(String email) {
        try {
            for (ParticipantModel participant : findAll()) {
                if (participant.getCorreo_participante().equals(email)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // *Verificamos que no hay dos correos con el mismo nombre de participante */

    public boolean nameExists(String name) {
        try {
            for (ParticipantModel participant : findAll()) {
                if (participant.getNombre_completo().equals(name)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
