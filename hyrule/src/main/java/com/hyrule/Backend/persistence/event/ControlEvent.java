package com.hyrule.Backend.persistence.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.model.event.EventModel;
import com.hyrule.Backend.persistence.Control;

/**
 * Controlador de persistencia para operaciones CRUD de eventos.
 * Gestiona la validación, inserción y consulta de eventos en la base de datos.
 */
public class ControlEvent extends Control<EventModel> {

    /** Conexión a la base de datos */
    private DBConnection dbConnection;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     */
    public ControlEvent() {
        this.dbConnection = new DBConnection();
        dbConnection.connect();
    }

    /**
     * Inserta un nuevo evento en la base de datos.
     * 
     * @param entity el evento a insertar
     * @return el evento insertado o null si falla
     */
    @Override
    public EventModel insert(EventModel entity) {

        // *Generamos el sql*/
        String sql = "INSERT INTO evento (codigo_evento, fecha_evento, tipo_evento, titulo_evento, ubicacion_evento, cupo_max_participantes, costo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            pstmt.setString(1, entity.getCodigoEvento());
            pstmt.setDate(2, java.sql.Date.valueOf(entity.getFechaEvento()));
            pstmt.setString(3, entity.getTipoEvento().name());
            pstmt.setString(4, entity.getTituloEvento());
            pstmt.setString(5, entity.getUbicacionEvento());
            pstmt.setInt(6, entity.getCupoMaxParticipantes());
            pstmt.setBigDecimal(7, entity.getCostoEvento());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                conn.rollback();
                throw new SQLException("No se pudo insertar el evento, no se afectaron filas.");
            }
            conn.commit();
            return entity;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Actualiza un evento existente.
     * 
     * @param entity el evento con los datos actualizados
     */
    @Override
    public void update(EventModel entity) {
        // Implementation for updating an event
    }

    /**
     * Elimina un evento por su código.
     * 
     * @param key el código del evento a eliminar
     */
    @Override
    public void delete(String key) {
        // Implementation for deleting an event by key
    }

    /**
     * Busca un evento por su código.
     * 
     * @param key el código del evento
     * @return el evento encontrado o null
     */
    @Override
    public EventModel findByKey(String key) {
        // Implementation for finding an event by key
        return null; // Placeholder return
    }

    /**
     * Obtiene todos los eventos registrados.
     * 
     * @return lista de todos los eventos
     */
    @Override
    public List<EventModel> findAll() {
        // Implementation for finding all events
        return null; // Placeholder return
    }

    /**
     * Valida que un evento no tenga conflictos antes de ser insertado.
     * 
     * @param event el evento a validar
     * @return "Ok" si es válido, mensaje de error si no
     */
    public String validateEvent(EventModel event) {
        if (existsByCode(event.getCodigoEvento())) {
            return "El código del evento ya existe.";
        }
        if (existsByTitleAndDate(event.getTituloEvento(), event.getFechaEvento().toString())) {
            return "Ya existe un evento con el mismo título en la misma fecha.";
        }

        return "Ok";

    }

    /**
     * Verifica si existe un evento con el código especificado.
     * 
     * @param code el código del evento a verificar
     * @return true si existe, false si no
     */
    public boolean existsByCode(String code) {
        String sql = "SELECT COUNT(*) FROM evento WHERE codigo_evento = ?";
        try (
                Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
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

    /**
     * Verifica si existe un evento con el mismo título y fecha.
     * 
     * @param title el título del evento
     * @param date  la fecha del evento en formato string
     * @return true si existe, false si no
     */
    public boolean existsByTitleAndDate(String title, String date) {
        String sql = "SELECT COUNT(*) FROM evento WHERE titulo_evento = ? AND DATE(fecha_evento) = ?";
        try (
                Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
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
