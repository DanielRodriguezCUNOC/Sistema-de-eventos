package com.hyrule.Backend.persistence.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.model.event.EventModel;
import com.hyrule.Backend.persistence.Control;

public class ControlEvent extends Control<EventModel> {

    @Override
    public EventModel insert(EventModel entity) {
        // *Generamos la query*/
        String query = "INSERT INTO evento (codigo_evento, fecha_evento, tipo_evento, titulo_evento, ubicacion_evento, cupo_max_participantes) VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = new DBConnection().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, entity.getCodigoEvento());
            pstmt.setDate(2, java.sql.Date.valueOf(entity.getFechaEvento()));
            pstmt.setString(3, entity.getTipoEvento().name());
            pstmt.setString(4, entity.getTituloEvento());
            pstmt.setString(5, entity.getUbicacionEvento());
            pstmt.setInt(6, entity.getCupoMaxParticipantes());

            int rowsAffected = pstmt.executeUpdate();

            return (rowsAffected > 0) ? entity : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Placeholder return
    }

    @Override
    public void update(EventModel entity) {
        // Implementation for updating an event
    }

    @Override
    public void delete(String key) {
        // Implementation for deleting an event by key
    }

    @Override
    public EventModel findByKey(String key) {
        // Implementation for finding an event by key
        return null; // Placeholder return
    }

    @Override
    public List<EventModel> findAll() {
        // Implementation for finding all events
        return null; // Placeholder return
    }

}
