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

            if (certificateExists(conn, entity)) {
                return null;
            }

            if (!existsAttendance(conn, entity)) {
                return null;
            }
            pstmt.setString(1, entity.getCodigoEvento());
            pstmt.setString(2, entity.getCorreoParticipante());
            int rowsAffected = pstmt.executeUpdate();
            return (rowsAffected > 0) ? entity : null;
        }
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

    // *Funcion para saber si un participante asistio a una actividad */
    private boolean existsAttendance(Connection connection, CertifiedModel entity) throws SQLException {
        String query = "SELECT COUNT(*) FROM asistencia a " +
                "JOIN actividad ac ON a.codigo_actividad = ac.codigo_actividad " +
                "WHERE a.correo_participante = ? " +
                "AND ac.codigo_evento = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, entity.getCorreoParticipante());
            stmt.setString(2, entity.getCodigoEvento());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private boolean certificateExists(Connection connection, CertifiedModel entity) throws SQLException {
        String query = "SELECT COUNT(*) FROM certificado WHERE codigo_evento = ? AND correo_participante = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, entity.getCodigoEvento());
            stmt.setString(2, entity.getCorreoParticipante());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

}
