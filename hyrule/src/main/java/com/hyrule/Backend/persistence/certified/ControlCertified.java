package com.hyrule.Backend.persistence.certified;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.hyrule.Backend.model.certified.CertifiedModel;
import com.hyrule.Backend.persistence.Control;

/**
 * Controlador de persistencia para operaciones CRUD de certificados.
 * Gestiona validaciones de asistencia y registros de certificados.
 */
public class ControlCertified extends Control<CertifiedModel> {

    /**
     * Inserta un nuevo certificado en la base de datos.
     * 
     * @param entity el certificado a insertar
     * @param conn   la conexión a la base de datos
     * @return el certificado insertado
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public CertifiedModel insert(CertifiedModel entity, Connection conn) throws SQLException {

        String query = "INSERT INTO certificado (codigo_evento, correo_participante) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);

            try {
                pstmt.setString(1, entity.getCodigoEvento());
                pstmt.setString(2, entity.getCorreoParticipante());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    throw new SQLException("No se pudo insertar el certificado, no se afectaron filas.");
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
     * Actualiza un certificado existente.
     * 
     * @param entity el certificado con datos actualizados
     * @param conn   la conexión a la base de datos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void update(CertifiedModel entity, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Elimina un certificado por clave.
     * 
     * @param key  la clave del certificado a eliminar
     * @param conn la conexión a la base de datos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void delete(String key, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    /**
     * Busca un certificado por clave.
     * 
     * @param key  la clave de búsqueda
     * @param conn la conexión a la base de datos
     * @return el certificado encontrado o null
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public CertifiedModel findByKey(String key, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    /**
     * Obtiene todos los certificados registrados.
     * 
     * @param conn la conexión a la base de datos
     * @return lista de todos los certificados
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public List<CertifiedModel> findAll(Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    /**
     * Valida un certificado verificando asistencia y duplicados.
     * 
     * @param certified el certificado a validar
     * @param conn      la conexión a la base de datos
     * @return "Ok" si es válido, mensaje de error si no
     */
    public String validateCertificate(CertifiedModel certified, Connection conn) {

        if (certificateExists(certified, conn)) {
            return "El certificado ya existe para el participante: " + certified.getCorreoParticipante()
                    + " en el evento: " + certified.getCodigoEvento();

        }
        try {
            if (!existsAttendance(certified, conn)) {
                return "El participante: " + certified.getCorreoParticipante()
                        + " no asistió a la actividad del evento: " + certified.getCodigoEvento();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al validar la asistencia del participante.";
        }

        return "Ok";
    }

    /**
     * Verifica si el participante asistió a alguna actividad del evento.
     * 
     * @param entity el certificado con datos del participante y evento
     * @param conn   la conexión a la base de datos
     * @return true si asistió, false si no
     * @throws SQLException si ocurre un error en la consulta
     */
    private boolean existsAttendance(CertifiedModel entity, Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM asistencia a " +
                "JOIN actividad ac ON a.codigo_actividad = ac.codigo_actividad " +
                "WHERE a.correo_participante = ? " +
                "AND ac.codigo_evento = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, entity.getCorreoParticipante());
            stmt.setString(2, entity.getCodigoEvento());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return false;
    }

    /**
     * Verifica si ya existe un certificado para el participante y evento.
     * 
     * @param entity el certificado a verificar
     * @param conn   la conexión a la base de datos
     * @return true si ya existe, false si no
     */
    private boolean certificateExists(CertifiedModel entity, Connection conn) {
        String query = "SELECT COUNT(*) FROM certificado WHERE codigo_evento = ? AND correo_participante = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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

    // *Obtenemos los datos para el certificado */

    public String[] getDataForCertified(String codigoEvento, String correoParticipante, Connection conn) {

        String query = "SELECT p.nombre_completo, p.tipo_participante, " +
                "e.titulo_evento, e.fecha_evento " +
                "FROM participante p " +
                "JOIN asistencia a ON p.correo_participante = a.correo_participante " +
                "JOIN actividad ac ON a.codigo_actividad = ac.codigo_actividad " +
                "JOIN evento e ON ac.codigo_evento = e.codigo_evento " +
                "WHERE e.codigo_evento = ? AND p.correo_participante = ? " +
                "LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codigoEvento);
            stmt.setString(2, correoParticipante);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nombreCompleto = rs.getString("nombre_completo");
                    String tipoParticipante = rs.getString("tipo_participante");
                    String tituloEvento = rs.getString("titulo_evento");
                    String fechaEvento = rs.getDate("fecha_evento").toString();

                    return new String[] { nombreCompleto, tipoParticipante, tituloEvento, fechaEvento };
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
