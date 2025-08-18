package com.hyrule.Backend.persistence.validate_registration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.model.validate_registration.ValidateRegistrationModel;
import com.hyrule.Backend.persistence.Control;

/**
 * Controlador de persistencia para validaciones de inscripciones.
 * Gestiona la verificación y registro de inscripciones válidas de
 * participantes.
 */
public class ControlValidateRegistration extends Control<ValidateRegistrationModel> {

    /** Conexión a la base de datos */
    private DBConnection dbConnection;
    private Connection conn;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     */
    public ControlValidateRegistration() {
        this.dbConnection = new DBConnection();
        this.conn = dbConnection.getConnection();
    }

    /**
     * Constructor que recibe una conexión existente.
     */
    public ControlValidateRegistration(Connection connection) {
        this.dbConnection = new DBConnection();
        this.conn = connection;
    }

    /**
     * Inserta una nueva validación de inscripción.
     * 
     * @param entity la validación a insertar
     * @return la validación insertada o null si falla
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public ValidateRegistrationModel insert(ValidateRegistrationModel entity) throws SQLException {

        // *Generamos el sql */
        String sql = "INSERT INTO validar_inscripcion (correo_participante, codigo_evento) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);) {

            conn.setAutoCommit(false);
            try {
                pstmt.setString(1, entity.getCorreo());
                pstmt.setString(2, entity.getCodigoEvento());
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    throw new SQLException("No se pudo insertar la validación, no se afectaron filas.");
                }
                conn.commit();
                return entity;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Error inesperado durante la inserción", e);
        }
    }

    /**
     * Actualiza una validación existente.
     * 
     * @param entity la validación con datos actualizados
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void update(ValidateRegistrationModel entity) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Elimina una validación por clave.
     * 
     * @param key la clave de la validación a eliminar
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void delete(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    /**
     * Busca una validación por clave.
     * 
     * @param key la clave de búsqueda
     * @return la validación encontrada o null
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public ValidateRegistrationModel findByKey(String key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    /**
     * Obtiene todas las validaciones registradas.
     * 
     * @return lista de todas las validaciones
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public List<ValidateRegistrationModel> findAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    /**
     * Valida una inscripción verificando que esté registrada y pagada.
     * 
     * @param registration la validación a procesar
     * @return "Ok" si es válida, mensaje de error si no
     */
    public String validateRegistration(ValidateRegistrationModel registration) {
        try {

            if (existsRegistration(registration)) {
                return "Ya se ha validado la inscripción para este participante y evento.";
            }

            if (!isRegisteredAndPaid(registration)) {
                return "El participante no está inscrito o no ha pagado.";
            }

            return "Ok";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al validar la inscripción: " + e.getMessage();
        }
    }

    /**
     * Verifica si el participante está inscrito y ha pagado el evento.
     * 
     * @param registration la validación con correo y código de evento
     * @return true si está inscrito y pagado
     * @throws SQLException si ocurre un error en la consulta
     */
    private boolean isRegisteredAndPaid(ValidateRegistrationModel registration) throws SQLException {

        // *Generamos el sql */

        String sql = "SELECT i.codigo_evento, p.correo_participante FROM inscripcion i JOIN pago p ON i.codigo_evento = p.codigo_evento AND i.correo_participante = p.correo_participante WHERE i.codigo_evento = ? AND i.correo_participante = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, registration.getCodigoEvento());
            pstmt.setString(2, registration.getCorreo());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica si ya existe una validación para el participante y evento.
     * 
     * @param registration la validación a verificar
     * @return true si ya existe, false si no
     * @throws SQLException si ocurre un error en la consulta
     */
    private boolean existsRegistration(ValidateRegistrationModel registration) throws SQLException {

        // *Generamos el sql */

        String sql = "SELECT COUNT(*) FROM validar_inscripcion WHERE correo_participante = ? AND codigo_evento = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, registration.getCorreo());

            pstmt.setString(2, registration.getCodigoEvento());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
