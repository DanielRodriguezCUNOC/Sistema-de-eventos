package com.hyrule.Backend.persistence.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.hyrule.Backend.model.reports.ParticipantModelReport;

public class ReportParticipantControl {

    /**
     * Obtiene los datos del reporte de participantes filtrados
     * 
     * @param codigoEvento     código del evento
     * @param tipoParticipante tipo de participante (puede ser vacío para incluir
     *                         todos)
     * @param institucion      institución (puede ser vacío para incluir todas)
     * @param conn             conexión a la base de datos
     * @return conjunto de participantes para el reporte
     */
    public Set<ParticipantModelReport> obtenerDatosReporte(String codigoEvento, String tipoParticipante,
            String institucion, Connection conn) {

        String sql = "SELECT p.correo_participante, " +
                "       p.tipo_participante, " +
                "       p.nombre_completo, " +
                "       p.institucion, " +
                "       CASE WHEN v.correo_participante IS NOT NULL THEN 'SI' ELSE 'NO' END AS validado " +
                "FROM participante p " +
                "JOIN inscripcion i ON p.correo_participante = i.correo_participante " +
                "LEFT JOIN validar_inscripcion v ON p.correo_participante = v.correo_participante AND i.codigo_evento = v.codigo_evento "
                +
                "WHERE i.codigo_evento = ? " +
                "  AND (? = '' OR p.tipo_participante = ?) " +
                "  AND (? = '' OR p.institucion = ?) " +
                "ORDER BY p.institucion, p.tipo_participante, p.nombre_completo";

        Set<ParticipantModelReport> resultados = new HashSet<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            // Establecer parámetros
            ps.setString(1, codigoEvento);
            ps.setString(2, tipoParticipante == null ? "" : tipoParticipante);
            ps.setString(3, tipoParticipante == null ? "" : tipoParticipante);
            ps.setString(4, institucion == null ? "" : institucion);
            ps.setString(5, institucion == null ? "" : institucion);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String correo = rs.getString("correo_participante");
                    String tipo = rs.getString("tipo_participante");
                    String nombre = rs.getString("nombre_completo");
                    String inst = rs.getString("institucion");
                    boolean validado = "SI".equals(rs.getString("validado"));

                    ParticipantModelReport reporte = new ParticipantModelReport(
                            correo, tipo, inst, nombre, validado);
                    resultados.add(reporte);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener datos del reporte de participantes: " + e.getMessage(), e);
        }

        return resultados;
    }

}
