package com.hyrule.Backend.persistence.asistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.model.asistencia.AttendanceModel;
import com.hyrule.Backend.persistence.Control;

public class ControlAttendance extends Control<AttendanceModel> {

    private DBConnection dbConnection;

    public ControlAttendance() {
        this.dbConnection = new DBConnection();
        dbConnection.connect();
    }

    @Override
    public AttendanceModel insert(AttendanceModel entity) throws SQLException {

        // *Generamos la query*/
        String query = "INSERT INTO asistencia (correo_participante, codigo_actividad) VALUES (?, ?)";

        try (Connection conn = dbConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, entity.getCorreoParticipante());

            pstmt.setString(2, entity.getCodigoActividad());

            pstmt.executeUpdate();
        }
        return entity;

    }

    @Override
    public void update(AttendanceModel entity) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public AttendanceModel findByKey(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    @Override
    public List<AttendanceModel> findAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    // *Funcion para validar la actividad */
    public String validateAttendance(AttendanceModel attendace) throws SQLException {
        if (existsAttendance(attendace)) {
            return "Este participante ya cuenta con asistencia en esta actividad.";
        }
        if (!hasCapacity(attendace.getCodigoActividad())) {
            return "La actividad ha alcanzado su cupo máximo.";
        }
        return "Ok";
    }

    // * Verificamos si la asistencia ya existe */
    private boolean existsAttendance(AttendanceModel entity) throws SQLException {
        String sql = "SELECT 1 FROM asistencia WHERE correo_participante = ? AND codigo_actividad = ?";
        try (Connection connection = dbConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, entity.getCorreoParticipante());
            pstmt.setString(2, entity.getCodigoActividad());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // * Verificamos si la actividad tiene cupo */
    private boolean hasCapacity(String codigoActividad) throws SQLException {

        String sql = "SELECT COUNT(*) as current_attendance, a.cupo_maximo " +
                "FROM asistencia a " +
                "JOIN actividad ac ON a.codigo_actividad = ac.codigo_actividad " +
                "WHERE a.codigo_actividad = ? " +
                "GROUP BY a.codigo_actividad";
        try (Connection connection = dbConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, codigoActividad);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int current = rs.getInt("current_attendance");
                    int max = rs.getInt("cupo_maximo");
                    return current < max;
                } else {
                    return true;
                }
            }
        }
    }

}
