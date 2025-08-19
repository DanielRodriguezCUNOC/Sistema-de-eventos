package com.hyrule.Backend.persistence.participant;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.persistence.Control;

/**
 * Controlador de persistencia para operaciones CRUD de participantes.
 * Gestiona validaciones de duplicados y registro de participantes.
 */
public class ControlParticipant extends Control<ParticipantModel> {

    /**
     * Inserta un nuevo participante en la base de datos.
     * 
     * @param entity el participante a insertar
     * @param conn   la conexión a la base de datos
     * @return el participante insertado
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public ParticipantModel insert(ParticipantModel entity, Connection conn) throws SQLException {

        // *Generamos la query*/
        String query = "INSERT INTO participante (correo_participante, nombre_completo, tipo_participante, institucion) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);

            try {
                pstmt.setString(1, entity.getCorreoParticipante());
                pstmt.setString(2, entity.getNombreCompleto());
                pstmt.setString(3, entity.getTipoParticipante().name());
                pstmt.setString(4, entity.getInstitucion());

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected == 0) {
                    conn.rollback();
                    throw new SQLException("No se pudo insertar el participante, no se afectaron filas.");
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
     * Actualiza un participante existente.
     * 
     * @param entity el participante con datos actualizados
     * @param conn   la conexión a la base de datos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void update(ParticipantModel entity, Connection conn) throws SQLException {

        String query = "UPDATE participante SET nombre_completo = ?, tipo_participante = ?, institucion = ? WHERE correo_participante = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, entity.getNombreCompleto());
            pstmt.setString(2, entity.getTipoParticipante().name());
            pstmt.setString(3, entity.getInstitucion());
            pstmt.setString(4, entity.getCorreoParticipante());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar el participante, no se afectaron filas.");
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Elimina un participante por clave.
     * 
     * @param key  la clave del participante a eliminar
     * @param conn la conexión a la base de datos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void delete(String key, Connection conn) throws SQLException {
        String query = "DELETE FROM participante WHERE correo_participante = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, key);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo eliminar el participante, no se afectaron filas.");
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Busca un participante por clave.
     * 
     * @param key  la clave de búsqueda
     * @param conn la conexión a la base de datos
     * @return el participante encontrado o null
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public ParticipantModel findByKey(String key, Connection conn) throws SQLException {
        return null;
    }

    /**
     * Obtiene todos los participantes registrados.
     * 
     * @param conn la conexión a la base de datos
     * @return lista de todos los participantes
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public List<ParticipantModel> findAll(Connection conn) throws SQLException {

        List<ParticipantModel> participants = new ArrayList<>();

        String query = "SELECT * FROM participante";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String correo = rs.getString("correo_participante");
                String nombre = rs.getString("nombre_completo");
                String tipoParticipante = rs.getString("tipo_participante");
                String institucion = rs.getString("institucion");

                ParticipantModel participant = new ParticipantModel(correo, nombre, tipoParticipante, institucion);
                participants.add(participant);
            }

        } catch (SQLException e) {
            throw e;
        }

        return participants;
    }

    /**
     * Valida un participante verificando duplicados por correo y nombre.
     * 
     * @param participant el participante a validar
     * @param conn        la conexión a la base de datos
     * @return "Ok" si es válido, mensaje de error si no
     */
    public String validateParticipant(ParticipantModel participant, Connection conn) {

        if (emailExists(participant.getCorreoParticipante(), conn)) {
            return "Hay un participante registrado con el mismo correo electrónico.";
        }
        if (nameExists(participant.getNombreCompleto(), conn)) {
            return "Hay un participante registrado con el mismo nombre.";
        }
        return "Ok";

    }

    /**
     * Verifica si ya existe un participante con el correo especificado.
     * 
     * @param email el correo a verificar
     * @param conn  la conexión a la base de datos
     * @return true si existe, false si no
     */
    public boolean emailExists(String email, Connection conn) {
        try {
            for (ParticipantModel participant : findAll(conn)) {
                if (participant.getCorreoParticipante().equals(email)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si ya existe un participante con el nombre especificado.
     * 
     * @param name el nombre a verificar
     * @param conn la conexión a la base de datos
     * @return true si existe, false si no
     */
    public boolean nameExists(String name, Connection conn) {
        try {
            for (ParticipantModel participant : findAll(conn)) {
                if (participant.getNombreCompleto().equals(name)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
