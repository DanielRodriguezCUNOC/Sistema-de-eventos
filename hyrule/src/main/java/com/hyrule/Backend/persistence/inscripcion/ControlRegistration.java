package com.hyrule.Backend.persistence.inscripcion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.model.inscripcion.RegistrationModel;
import com.hyrule.Backend.persistence.Control;

/**
 * Controlador de persistencia para operaciones CRUD de inscripciones.
 * Gestiona validaciones de duplicados y registro de inscripciones de
 * participantes.
 */
public class ControlRegistration extends Control<RegistrationModel> {

    /**
     * Inserta una nueva inscripción en la base de datos.
     * 
     * @param entity la inscripción a insertar
     * @param conn   la conexión a la base de datos
     * @return la inscripción insertada
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public RegistrationModel insert(RegistrationModel entity, Connection conn) throws SQLException {

        String sql = "INSERT INTO inscripcion (codigo_evento, correo_participante, tipo_inscripcion) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            try {
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
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Actualiza una inscripción existente.
     * 
     * @param entity la inscripción con datos actualizados
     * @param conn   la conexión a la base de datos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void update(RegistrationModel entity, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Elimina una inscripción por clave.
     * 
     * @param key  la clave de la inscripción a eliminar
     * @param conn la conexión a la base de datos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void delete(String key, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    /**
     * Busca una inscripción por clave.
     * 
     * @param key  la clave de búsqueda
     * @param conn la conexión a la base de datos
     * @return la inscripción encontrada o null
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public RegistrationModel findByKey(String key, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    /**
     * Obtiene todas las inscripciones registradas.
     * 
     * @param conn la conexión a la base de datos
     * @return lista de todas las inscripciones
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public List<RegistrationModel> findAll(Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    /**
     * Valida una inscripción verificando duplicados y existencia de evento y
     * participante.
     * 
     * @param inscripcion la inscripción a validar
     * @param conn        la conexión a la base de datos
     * @return "Ok" si es válida, mensaje de error si no
     */
    public String validateInscripcion(RegistrationModel inscripcion, Connection conn) {

        if (existsInscription(inscripcion, conn)) {
            return "La inscripción ya existe";
        }

        if (!existsEvent(inscripcion.getCodigoEvento(), conn)) {
            return "El evento no existe";
        }

        if (!existsParticipant(inscripcion.getCorreoParticipante(), conn)) {
            return "El participante no existe";
        }

        return "Ok";
    }

    /**
     * Verifica si ya existe una inscripción para el evento y participante
     * especificados.
     * 
     * @param inscripcion la inscripción a verificar
     * @param conn        la conexión a la base de datos
     * @return true si ya existe, false si no
     */
    private boolean existsInscription(RegistrationModel inscripcion, Connection conn) {

        String query = "SELECT 1 FROM inscripcion WHERE codigo_evento = ? AND correo_participante = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, inscripcion.getCodigoEvento());
            pstmt.setString(2, inscripcion.getCorreoParticipante());

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica si existe un evento con el código especificado.
     * 
     * @param eventCode el código del evento a verificar
     * @param conn      la conexión a la base de datos
     * @return true si existe, false si no
     */
    public boolean existsEvent(String eventCode, Connection conn) {
        String query = "SELECT 1 FROM evento WHERE codigo_evento = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, eventCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica si existe un participante con el correo especificado.
     * 
     * @param participantEmail el correo del participante a verificar
     * @param conn             la conexión a la base de datos
     * @return true si existe, false si no
     */
    public boolean existsParticipant(String participantEmail, Connection conn) {
        String query = "SELECT 1 FROM participante WHERE correo_participante = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, participantEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
