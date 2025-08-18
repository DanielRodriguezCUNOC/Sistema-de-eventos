package com.hyrule.Backend.persistence.activity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.hyrule.Backend.model.activity.ActivityModel;
import com.hyrule.Backend.persistence.Control;

/**
 * Controlador de persistencia para operaciones CRUD de actividades.
 * Gestiona validaciones de horarios y registros de actividades de eventos.
 */
public class ControlActivity extends Control<ActivityModel> {

    /**
     * Inserta una nueva actividad en la base de datos.
     * 
     * @param entity la actividad a insertar
     * @param conn   la conexión a la base de datos
     * @return la actividad insertada
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public ActivityModel insert(ActivityModel entity, Connection conn) throws SQLException {

        // *Generamos la query*/
        String query = "INSERT INTO actividad (codigo_actividad, codigo_evento, correo_ponente, titulo_actividad,  tipo_actividad, cupo_maximo, hora_inicio, hora_fin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);

            try {
                pstmt.setString(1, entity.getCodigoActividad());
                pstmt.setString(2, entity.getCodigoEvento());
                pstmt.setString(3, entity.getCorreo());
                pstmt.setString(4, entity.getTituloActividad());
                pstmt.setString(5, entity.getTipoActividad().toString());
                pstmt.setInt(6, entity.getCupoMaximo());
                pstmt.setString(7, entity.getHoraInicio().toString());
                pstmt.setString(8, entity.getHoraFin().toString());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    throw new SQLException("No se pudo insertar la actividad, no se afectaron filas.");
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
     * Actualiza una actividad existente.
     * 
     * @param entity la actividad con datos actualizados
     * @param conn   la conexión a la base de datos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void update(ActivityModel entity, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Elimina una actividad por clave.
     * 
     * @param key  la clave de la actividad a eliminar
     * @param conn la conexión a la base de datos
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public void delete(String key, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    /**
     * Busca una actividad por clave.
     * 
     * @param key  la clave de búsqueda
     * @param conn la conexión a la base de datos
     * @return la actividad encontrada o null
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public ActivityModel findByKey(String key, Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKey'");
    }

    /**
     * Obtiene todas las actividades registradas.
     * 
     * @param conn la conexión a la base de datos
     * @return lista de todas las actividades
     * @throws SQLException si ocurre un error en la base de datos
     */
    @Override
    public List<ActivityModel> findAll(Connection conn) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    /**
     * Valida una actividad verificando conflictos de horarios y duplicados.
     * 
     * @param activity la actividad a validar
     * @param conn     la conexión a la base de datos
     * @return "Ok" si es válida, mensaje de error si no
     */
    public String validateActivity(ActivityModel activity, Connection conn) {

        if (existsByNameAndTime(activity.getCodigoEvento(), activity.getHoraInicio().toString(),
                activity.getHoraFin().toString(), conn)) {
            return "Ya existe una actividad con el mismo nombre y hora en el evento.";

        }
        if (existsByCode(activity.getCodigoActividad(), activity.getCodigoEvento(), conn)) {
            return "Ya existe una actividad con el mismo código en el evento.";
        }

        if (!existsByEventAndParticipant(activity.getCodigoEvento(), activity.getCorreo(), conn)) {
            return "El evento o el ponente no están registrados.";
        }

        return "Ok";
    }

    /**
     * Verifica si ya existe una actividad con el mismo código en el evento.
     * 
     * @param codigoActividad el código de la actividad a verificar
     * @param codigoEvento    el código del evento
     * @param conn            la conexión a la base de datos
     * @return true si ya existe, false si no
     */
    public boolean existsByCode(String codigoActividad, String codigoEvento, Connection conn) {
        String query = "SELECT COUNT(*) FROM actividad WHERE codigo_actividad = ? AND codigo_evento = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, codigoActividad);
            pstmt.setString(2, codigoEvento);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica si ya existe una actividad con los mismos horarios en el evento.
     * 
     * @param codigoEvento el código del evento
     * @param horaInicio   la hora de inicio de la actividad
     * @param horaFin      la hora de fin de la actividad
     * @param conn         la conexión a la base de datos
     * @return true si ya existe conflicto de horarios, false si no
     */
    public boolean existsByNameAndTime(String codigoEvento, String horaInicio, String horaFin, Connection conn) {
        String query = "SELECT COUNT(*) FROM actividad WHERE codigo_evento = ? AND hora_inicio = ? AND hora_fin = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, codigoEvento);
            pstmt.setString(2, horaInicio);
            pstmt.setString(3, horaFin);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica si existen el evento y el participante (ponente) especificados.
     * 
     * @param codigoEvento  el código del evento a verificar
     * @param correoPonente el correo del ponente a verificar
     * @param conn          la conexión a la base de datos
     * @return true si ambos existen, false si no
     */
    public boolean existsByEventAndParticipant(String codigoEvento, String correoPonente, Connection conn) {
        String query = "SELECT (SELECT COUNT(*) FROM evento WHERE codigo_evento = ?) AS exists_event, (SELECT COUNT(*) FROM participante WHERE correo_participante = ?) AS exists_participant";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, codigoEvento);
            pstmt.setString(2, correoPonente);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("exists_event") > 0 && rs.getInt("exists_participant") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
