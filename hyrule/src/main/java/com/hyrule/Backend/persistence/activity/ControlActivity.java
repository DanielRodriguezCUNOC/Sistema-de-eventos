package com.hyrule.Backend.persistence.activity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.connection.DBConnection;
import com.hyrule.Backend.model.activity.ActivityModel;
import com.hyrule.Backend.persistence.Control;

public class ControlActivity extends Control<ActivityModel> {

    @Override
    public ActivityModel insert(ActivityModel entity) throws SQLException {

        // *Generamos la query*/
        String query = "INSERT INTO actividad (codigo_actividad, codigo_evento, correo_ponente, titulo_actividad,  tipo_actividad, cupo_maximo, hora_inicio, hora_fin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = new DBConnection().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, entity.getCodigoActividad());
            pstmt.setString(2, entity.getCodigoEvento());
            pstmt.setString(3, entity.getCorreo());
            pstmt.setString(4, entity.getTituloActividad());
            pstmt.setString(5, entity.getTipoActividad().toString());
            pstmt.setInt(6, entity.getCupoMaximo());
            pstmt.setString(7, entity.getHoraInicio().toString());
            pstmt.setString(8, entity.getHoraFin().toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public void update(ActivityModel entity) {
    }

    @Override
    public void delete(String key) {
    }

    @Override
    public ActivityModel findByKey(String key) {
        return null;
    }

    @Override
    public List<ActivityModel> findAll() throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    // *Funcion para validar la actividad */

    public String validateActivity(ActivityModel activity) {

        if (existsByNameAndTime(activity.getCodigoEvento(), activity.getHoraInicio().toString(),
                activity.getHoraFin().toString())) {
            return "Ya existe una actividad con el mismo nombre y hora en el evento.";

        }
        if (existsByCode(activity.getCodigoActividad(), activity.getCodigoEvento())) {
            return "Ya existe una actividad con el mismo código en el evento.";
        }

        if (!existsByEventAndParticipant(activity.getCodigoEvento(), activity.getCorreo())) {
            return "El evento o el ponente no están registrados.";
        }

        return "Ok";
    }

    // *Funcion para evitar duplicaciones de actividades */
    public boolean existsByCode(String codigoActividad, String codigoEvento) {
        String query = "SELECT COUNT(*) FROM actividad WHERE codigo_actividad = ? AND codigo_evento = ?";

        try (Connection conn = new DBConnection().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, codigoActividad);
            pstmt.setString(2, codigoEvento);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // *Funcion para buscar si una actividad tiene el mismo nombre y hora */

    public boolean existsByNameAndTime(String codigoEvento, String horaInicio, String horaFin) {
        String query = "SELECT COUNT(*) FROM actividad WHERE codigo_evento = ? AND hora_inicio = ? AND hora_fin = ?";

        try (Connection conn = new DBConnection().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, codigoEvento);
            pstmt.setString(2, horaInicio);
            pstmt.setString(3, horaFin);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // *Funcion para verificar que el evento y el participante exista */

    public boolean existsByEventAndParticipant(String codigoEvento, String correoPonente) {
        String query = "SELECT (SELECT COUNT(*) FROM evento WHERE codigo_evento = ?) AS exists_event, (SELECT COUNT(*) FROM participante WHERE correo_participante = ?) AS exists_participant";

        try (Connection conn = new DBConnection().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, codigoEvento);
            pstmt.setString(2, correoPonente);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("exists_event") > 0 && rs.getInt("exists_participant") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
