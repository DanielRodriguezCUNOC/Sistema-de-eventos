package com.hyrule.Backend.persistence.participant;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.persistence.Control;

/**
 * Controlador de persistencia para operaciones CRUD de participantes.
 * Gestiona validaciones de duplicados y registro de participantes.
 */
public class ControlParticipant extends Control<ParticipantModel> {

    /** Conexión a la base de datos */
    private final DBConnection dbConnection;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     */
    public ControlParticipant() {
        this.dbConnection = new DBConnection();
    }

    /**
     * Inserta un nuevo participante en la base de datos.
     * 
     * @param entity el participante a insertar
     * @return el participante insertado o null si falla
     */
    @Override
    public ParticipantModel insert(ParticipantModel entity) {

        // *Generamos la query*/
        String query = "INSERT INTO participante (correo_participante, nombre_completo, tipo_participante, institucion) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            conn.setAutoCommit(false);

            pstmt.setString(1, entity.getCorreo_participante());
            pstmt.setString(2, entity.getNombre_completo());
            pstmt.setString(3, entity.getTipoParticipante().name());
            pstmt.setString(4, entity.getInstitucion());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                conn.commit();
                throw new SQLException("No se pudo insertar el participante, no se afectaron filas.");
            }
            conn.commit();
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Actualiza un participante existente.
     * 
     * @param entity el participante con datos actualizados
     */
    @Override
    public void update(ParticipantModel entity) {
    }

    /**
     * Elimina un participante por clave.
     * 
     * @param key la clave del participante a eliminar
     */
    @Override
    public void delete(String key) {
    }

    /**
     * Busca un participante por clave.
     * 
     * @param key la clave de búsqueda
     * @return el participante encontrado o null
     */
    @Override
    public ParticipantModel findByKey(String key) {
        return null;
    }

    /**
     * Obtiene todos los participantes registrados.
     * 
     * @return lista de todos los participantes
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public List<ParticipantModel> findAll() throws SQLException {

        List<ParticipantModel> participants = new ArrayList<>();

        String query = "SELECT * FROM participante";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
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
            e.printStackTrace();
            throw e;
        }

        return participants;
    }

    /**
     * Valida un participante verificando duplicados por correo y nombre.
     * 
     * @param participant el participante a validar
     * @return "Ok" si es válido, mensaje de error si no
     */
    public String validateParticipant(ParticipantModel participant) {

        if (emailExists(participant.getCorreo_participante())) {
            return "Hay un participante registrado con el mismo correo electrónico.";
        }
        if (nameExists(participant.getNombre_completo())) {
            return "Hay un participante registrado con el mismo nombre.";
        }
        return "Ok";

    }

    /**
     * Verifica si ya existe un participante con el correo especificado.
     * 
     * @param email el correo a verificar
     * @return true si existe, false si no
     */
    public boolean emailExists(String email) {
        try {
            for (ParticipantModel participant : findAll()) {
                if (participant.getCorreo_participante().equals(email)) {
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
     * @return true si existe, false si no
     */
    public boolean nameExists(String name) {
        try {
            for (ParticipantModel participant : findAll()) {
                if (participant.getNombre_completo().equals(name)) {
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
