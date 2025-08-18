package com.hyrule.Backend.persistence.certified;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.model.certified.CertifiedModel;
import com.hyrule.Backend.persistence.Control;

/**
 * Controlador de persistencia para operaciones CRUD de certificados.
 * Gestiona validaciones de asistencia y registros de certificados.
 */
public class ControlCertified extends Control<CertifiedModel> {

    /** Conexión a la base de datos */
    private DBConnection dbConnection;

    /**
     * Constructor que inicializa y conecta a la base de datos.
     */
    public ControlCertified() {
        this.dbConnection = new DBConnection();
        dbConnection.connect();
    }

    /**
     * Inserta un nuevo certificado en la base de datos.
     * 
     * @param entity el certificado a insertar
     * @return el certificado insertado
     * @throws SQLException si ocurre un error en la base de datos
     */
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

    /**
     * Actualiza un certificado existente.
     * 
     * @param entity el certificado con datos actualizados
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void update(CertifiedModel entity) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Elimina un certificado por clave.
     * 
     * @param key la clave del certificado a eliminar
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void delete(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    /**
     * Busca un certificado por clave.
     * 
     * @param key la clave de búsqueda
     * @return el certificado encontrado o null
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public CertifiedModel findByKey(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    /**
     * Obtiene todos los certificados registrados.
     * 
     * @return lista de todos los certificados
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public List<CertifiedModel> findAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    /**
     * Valida un certificado verificando asistencia y duplicados.
     * 
     * @param certified el certificado a validar
     * @return "Ok" si es válido, mensaje de error si no
     */
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

    /**
     * Verifica si el participante asistió a alguna actividad del evento.
     * 
     * @param entity el certificado con datos del participante y evento
     * @return true si asistió, false si no
     * @throws SQLException si ocurre un error en la consulta
     */
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

    /**
     * Verifica si ya existe un certificado para el participante y evento.
     * 
     * @param entity el certificado a verificar
     * @return true si ya existe, false si no
     */
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
