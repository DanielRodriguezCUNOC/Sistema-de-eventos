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

        try (Connection conn = dbConnection.getConnection()) {

            if (existsAttendance(conn, entity)) {
                return null; // Ya existe
            }

            if (!hasCapacity(conn, entity)) {
                return null; // Cupo lleno
            }

            return insertAttendance(conn, entity);
        }

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

    private boolean existsAttendance(Connection connection, AttendanceModel entity) throws SQLException {
        String sql = "SELECT 1 FROM asistencia WHERE correo_participante = ? AND codigo_actividad = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, entity.getCorreoParticipante());
            stmt.setString(2, entity.getCodigoActividad());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean hasCapacity(Connection connection, AttendanceModel entity) throws SQLException {
        String sql = "SELECT COUNT(*) as current_attendance, a.cupo_maximo " +
                "FROM asistencia a " +
                "JOIN actividad ac ON a.codigo_actividad = ac.codigo_actividad " +
                "WHERE a.codigo_actividad = ? " +
                "GROUP BY a.codigo_actividad";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, entity.getCodigoActividad());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int current = rs.getInt("current_attendance");
                    int max = rs.getInt("cupo_maximo");
                    return current < max;
                } else {
                    return true; // No hay registros, entonces hay cupo
                }
            }
        }
    }

    private AttendanceModel insertAttendance(Connection connection, AttendanceModel entity) throws SQLException {
        String sql = "INSERT INTO asistencia (correo_participante, codigo_actividad) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, entity.getCorreoParticipante());
            stmt.setString(2, entity.getCodigoActividad());
            int affected = stmt.executeUpdate();
            return affected == 1 ? entity : null;
        }
    }

}
