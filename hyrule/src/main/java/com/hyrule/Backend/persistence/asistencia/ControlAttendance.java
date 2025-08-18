package com.hyrule.Backend.persistence.asistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.model.asistencia.AttendanceModel;
import com.hyrule.Backend.persistence.Control;

/**
 * Controlador de persistencia para operaciones CRUD de asistencias.
 * Gestiona validaciones de cupo y registros de asistencia a actividades.
 */
public class ControlAttendance extends Control<AttendanceModel> {

    /** Conexión a la base de datos */
    private DBConnection dbConnection;

    /**
     * Constructor que inicializa y conecta a la base de datos.
     */
    public ControlAttendance() {
        this.dbConnection = new DBConnection();
        dbConnection.connect();
    }

    /**
     * Inserta una nueva asistencia en la base de datos.
     * 
     * @param entity la asistencia a insertar
     * @return la asistencia insertada
     * @throws SQLException si ocurre un error en la base de datos
     */
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

    /**
     * Actualiza una asistencia existente.
     * 
     * @param entity la asistencia con datos actualizados
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void update(AttendanceModel entity) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Elimina una asistencia por clave.
     * 
     * @param key la clave de la asistencia a eliminar
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void delete(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    /**
     * Busca una asistencia por clave.
     * 
     * @param key la clave de búsqueda
     * @return la asistencia encontrada o null
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public AttendanceModel findByKey(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    /**
     * Obtiene todas las asistencias registradas.
     * 
     * @return lista de todas las asistencias
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public List<AttendanceModel> findAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    /**
     * Valida una asistencia verificando duplicados y disponibilidad de cupo.
     * 
     * @param attendace la asistencia a validar
     * @return "Ok" si es válida, mensaje de error si no
     * @throws SQLException si ocurre un error en la consulta
     */
    public String validateAttendance(AttendanceModel attendace) throws SQLException {
        if (existsAttendance(attendace)) {
            return "Este participante ya cuenta con asistencia en esta actividad.";
        }
        if (!hasCapacity(attendace.getCodigoActividad())) {
            return "La actividad ha alcanzado su cupo máximo.";
        }
        return "Ok";
    }

    /**
     * Verifica si ya existe asistencia para el participante en la actividad.
     * 
     * @param entity la asistencia a verificar
     * @return true si ya existe, false si no
     * @throws SQLException si ocurre un error en la consulta
     */
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

    /**
     * Verifica si la actividad tiene cupo disponible.
     * 
     * @param codigoActividad el código de la actividad a verificar
     * @return true si tiene cupo disponible, false si está llena
     * @throws SQLException si ocurre un error en la consulta
     */
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
