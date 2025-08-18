package com.hyrule.Backend.persistence.asistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.model.asistencia.AttendanceModel;
import com.hyrule.Backend.persistence.Control;

/**
 * Controlador de persistencia para operaciones CRUD de asistencias.
 * Gestiona validaciones de cupo y registros de asistencia a actividades.
 */
public class ControlAttendance extends Control<AttendanceModel> {

    /**
     * Constructor del controlador de asistencias.
     */
    public ControlAttendance() {
    }

    /**
     * Inserta una nueva asistencia en la base de datos.
     * 
     * @param entity la asistencia a insertar
     * @param conn   la conexión a la base de datos
     * @return la asistencia insertada
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public AttendanceModel insert(AttendanceModel entity, Connection conn) throws SQLException {

        // *Generamos la query*/
        String query = "INSERT INTO asistencia (correo_participante, codigo_actividad) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);

            try {
                pstmt.setString(1, entity.getCorreoParticipante());
                pstmt.setString(2, entity.getCodigoActividad());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    throw new SQLException("No se pudo insertar la asistencia, no se afectaron filas.");
                }
                conn.commit();
                return entity;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Actualiza una asistencia existente.
     * 
     * @param entity la asistencia con datos actualizados
     * @param conn   la conexión a la base de datos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void update(AttendanceModel entity, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Elimina una asistencia por clave.
     * 
     * @param key  la clave de la asistencia a eliminar
     * @param conn la conexión a la base de datos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void delete(String key, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    /**
     * Busca una asistencia por clave.
     * 
     * @param key  la clave de búsqueda
     * @param conn la conexión a la base de datos
     * @return la asistencia encontrada o null
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public AttendanceModel findByKey(String key, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    /**
     * Obtiene todas las asistencias registradas.
     * 
     * @param conn la conexión a la base de datos
     * @return lista de todas las asistencias
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public List<AttendanceModel> findAll(Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    /**
     * Valida una asistencia verificando duplicados y disponibilidad de cupo.
     * 
     * @param attendace la asistencia a validar
     * @param conn      la conexión a la base de datos
     * @return "Ok" si es válida, mensaje de error si no
     * @throws SQLException si ocurre un error en la consulta
     */
    public String validateAttendance(AttendanceModel attendace, Connection conn) throws SQLException {
        if (existsAttendance(attendace, conn)) {
            return "Este participante ya cuenta con asistencia en esta actividad.";
        }
        if (!hasCapacity(attendace.getCodigoActividad(), conn)) {
            return "La actividad ha alcanzado su cupo máximo.";
        }
        return "Ok";
    }

    /**
     * Verifica si ya existe asistencia para el participante en la actividad.
     * 
     * @param entity la asistencia a verificar
     * @param conn   la conexión a la base de datos
     * @return true si ya existe, false si no
     * @throws SQLException si ocurre un error en la consulta
     */
    private boolean existsAttendance(AttendanceModel entity, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM asistencia WHERE correo_participante = ? AND codigo_actividad = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
     * @param conn            la conexión a la base de datos
     * @return true si tiene cupo disponible, false si está llena
     * @throws SQLException si ocurre un error en la consulta
     */
    private boolean hasCapacity(String codigoActividad, Connection conn) throws SQLException {

        String sql = "SELECT COUNT(ast.correo_participante) as current_attendance, ac.cupo_maximo " +
                "FROM actividad ac " +
                "LEFT JOIN asistencia ast ON ac.codigo_actividad = ast.codigo_actividad " +
                "WHERE ac.codigo_actividad = ? " +
                "GROUP BY ac.codigo_actividad, ac.cupo_maximo";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigoActividad);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int current = rs.getInt("current_attendance");
                    int max = rs.getInt("cupo_maximo");
                    return current < max;
                } else {
                    // Si no hay registros, verificar que la actividad existe
                    String checkSql = "SELECT cupo_maximo FROM actividad WHERE codigo_actividad = ?";
                    try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                        checkPstmt.setString(1, codigoActividad);
                        try (ResultSet checkRs = checkPstmt.executeQuery()) {
                            return checkRs.next(); // true si existe la actividad
                        }
                    }
                }
            }
        }
    }

}
