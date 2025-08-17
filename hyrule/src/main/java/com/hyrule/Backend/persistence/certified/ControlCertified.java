package com.hyrule.Backend.persistence.certified;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.model.certified.CertifiedModel;
import com.hyrule.Backend.persistence.Control;

public class ControlCertified extends Control<CertifiedModel> {

    private DBConnection dbConnection;

    public ControlCertified() {
        this.dbConnection = new DBConnection();
        dbConnection.connect();
    }

    @Override
    public CertifiedModel insert(CertifiedModel entity) throws SQLException {

        String query = "INSERT INTO certificado (codigo_evento, correo_participante) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, entity.getCodigoEvento());

            pstmt.setString(2, entity.getCorreoParticipante());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public void update(CertifiedModel entity) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public CertifiedModel findByKey(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    @Override
    public List<CertifiedModel> findAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    public String validateCertificate(CertifiedModel certified) {

        if (!certificateExists(certified)) {
            return "El certificado ya existe para el participante: " + certified.getCorreoParticipante()
                    + " en el evento: " + certified.getCodigoEvento();

        }
        try {
            if (!existsAttendance(certified)) {
                return "El participante: " + certified.getCorreoParticipante()
                        + " no asistió a la actividad del evento: " + certified.getCodigoEvento();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al validar la asistencia del participante.";
        }

        return "Ok";
    }

    // *Funcion para saber si un participante asistio a una actividad */
    private boolean existsAttendance(CertifiedModel entity) throws SQLException {
        String query = "SELECT COUNT(*) FROM asistencia a " +
                "JOIN actividad ac ON a.codigo_actividad = ac.codigo_actividad " +
                "WHERE a.correo_participante = ? " +
                "AND ac.codigo_evento = ?";

        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(query)) {

            stmt.setString(1, entity.getCorreoParticipante());

            stmt.setString(2, entity.getCodigoEvento());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // *Funcion para validar el certificado */

    // *Funcion para validar si el certificado ya existe */
    private boolean certificateExists(CertifiedModel entity) {
        String query = "SELECT COUNT(*) FROM certificado WHERE codigo_evento = ? AND correo_participante = ?";

        try (
                PreparedStatement stmt = dbConnection.getConnection().prepareStatement(query)) {
            stmt.setString(1, entity.getCodigoEvento());
            stmt.setString(2, entity.getCorreoParticipante());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
