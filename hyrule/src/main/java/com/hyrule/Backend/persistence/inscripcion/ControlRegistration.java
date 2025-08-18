package com.hyrule.Backend.persistence.inscripcion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.model.inscripcion.RegistrationModel;
import com.hyrule.Backend.persistence.Control;

public class ControlRegistration extends Control<RegistrationModel> {

    private final DBConnection dbConnection;

    public ControlRegistration() {
        this.dbConnection = new DBConnection();
    }

    @Override
    public RegistrationModel insert(RegistrationModel entity) throws SQLException {

        String sql = "INSERT INTO inscripcion (codigo_evento, correo_participante, tipo_inscripcion) VALUES (?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            pstmt.setString(1, entity.getCodigoEvento());
            pstmt.setString(2, entity.getCorreoParticipante());
            pstmt.setString(3, entity.getTipoInscripcion().name());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                conn.rollback();
                throw new SQLException("No se pudo insertar la inscripción, no se afectaron filas.");
            }
            conn.commit();
            return entity;

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;

        }
    }

    @Override
    public void update(RegistrationModel entity) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public RegistrationModel findByKey(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    @Override
    public List<RegistrationModel> findAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    // *Validamos la inscripción */
    public String validateInscripcion(RegistrationModel inscripcion) {
        if (existsInscription(inscripcion)) {
            return "La inscripción ya existe";
        }
        return "Ok";
    }

    // *Verificamos que una inscripcion ya exista */
    private boolean existsInscription(RegistrationModel inscripcion) {

        // *Generar la query */
        String query = "SELECT * FROM inscripcion WHERE codigo_evento = ? AND correo_participante = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, inscripcion.getCodigoEvento());
            pstmt.setString(2, inscripcion.getCorreoParticipante());

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
    }

    // *Verificamos */

}
