package com.hyrule.Backend.reports;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;
import java.util.Set;

import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.activity.ActivityModel;
import com.hyrule.Backend.model.asistencia.AttendanceModel;
import com.hyrule.Backend.model.event.EventModel;
import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.persistence.certified.ControlCertified;

public class CertifiedCreator {

    ValidationArchive validator = ValidationArchive.getInstance();

    /**
     * Crea un certificado para un participante en un evento.
     * 
     * @param codigoEvento       el código del evento
     * @param correoParticipante el correo del participante
     * @return un mensaje de éxito o error
     */
    public String createCertificateFromArchive(String correoParticipante, String codigoEvento, Path certifiedPath) {

        try {

            InputStream templateStream = getClass().getClassLoader()
                    .getResourceAsStream("templates/certified_template.html");

            String template = new String(templateStream.readAllBytes(), StandardCharsets.UTF_8);

            String[] datos = generarCertificado(codigoEvento, correoParticipante);

            if (datos == null) {
                return "Error al generar el certificado: datos no encontrados.";
            }

            template = template.replace("Joe Nathan", datos[0]); // nombre completo
            template = template.replace("Certificate of Completion", "Certificado de Participación"); // título general
            template = template.replace("For deftly defying the laws of gravity<br/>and flying high",
                    "Como " + datos[1] + " en el evento: <br/><b>" + datos[2] + "</b><br/>Fecha: " + datos[3]);

            String certifiedName = "Certificado" + datos[0].replace(" ", "_") + "_" + datos[2].replace(" ", "_")
                    + ".html";
            certifiedPath = certifiedPath.resolve(certifiedName);
            try (BufferedWriter bw = Files.newBufferedWriter(certifiedPath, StandardCharsets.UTF_8)) {
                bw.write(template);
            }
            return "Ok";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error al crear el certificado.";

    }

    public void createCertificateFromForm(String codigoEvento, String correoParticipante, Connection conn,
            ControlCertified controlCertified, Path certifiedPath) {

        String[] data = getDataForCertified(codigoEvento, correoParticipante, conn, controlCertified);

        try {

            InputStream templateStream = getClass().getClassLoader()
                    .getResourceAsStream("templates/certified_template.html");

            String template = new String(templateStream.readAllBytes(), StandardCharsets.UTF_8);

            template = template.replace("Joe Nathan", data[0]); // nombre completo
            template = template.replace("Certificate of Completion", "Certificado de Participación"); // título general
            template = template.replace("For deftly defying the laws of gravity<br/>and flying high",
                    "Como " + data[1] + " en el evento: <br/><b>" + data[2] + "</b><br/>Fecha: " + data[3]);

            try (BufferedWriter bw = Files.newBufferedWriter(certifiedPath, StandardCharsets.UTF_8)) {
                bw.write(template);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // *Obtenemos los datos desde la base de datos */

    private String[] getDataForCertified(String codigoEvento, String correoParticipante, Connection conn,
            ControlCertified control) {

        String[] data = new String[4];
        data = control.getDataForCertified(codigoEvento, correoParticipante, conn);

        return data;
    }

    // *Metodo para encontrar los datos para el certificado */
    private String[] generarCertificado(String codigoEvento, String correoParticipante) {

        String[] datosCertificado = null;

        Set<EventModel> eventos = validator.getEventos();
        Set<AttendanceModel> asistencias = validator.getAsistencias();
        Set<ParticipantModel> participantes = validator.getParticipantes();
        Set<ActivityModel> actividades = validator.getActividades();

        EventModel evento = eventos.stream()
                .filter(e -> e.getCodigoEvento().equals(codigoEvento))
                .findFirst()
                .orElse(null);

        ParticipantModel participante = participantes.stream()
                .filter(p -> p.getCorreoParticipante().equals(correoParticipante))
                .findFirst()
                .orElse(null);

        if (participante == null || evento == null)
            return datosCertificado;

        List<String> coigosActividad = asistencias.stream()
                .filter(a -> a.getCorreoParticipante().equals(correoParticipante))
                .map(AttendanceModel::getCodigoActividad)
                .toList();

        List<ActivityModel> actividadesEvento = actividades.stream()
                .filter(ac -> coigosActividad.contains(ac.getCodigoActividad())
                        && ac.getCodigoEvento().equals(codigoEvento))
                .toList();
        if (actividadesEvento.isEmpty())
            return datosCertificado;

        EventModel eventoCertificable = eventos.stream()
                .filter(e -> e.getCodigoEvento().equals(codigoEvento))
                .findFirst()
                .orElse(null);
        if (eventoCertificable == null)
            return datosCertificado;

        datosCertificado = new String[] {
                participante.getNombreCompleto(),
                participante.getTipoParticipante().toString().toLowerCase(),
                eventoCertificable.getTituloEvento(),
                eventoCertificable.getFechaEvento().toString()
        };

        return datosCertificado;
    }
}