package com.hyrule.Backend.reports;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.hyrule.Backend.Validation.ValidationArchive;
import com.hyrule.Backend.model.inscripcion.RegistrationModel;
import com.hyrule.Backend.model.participant.ParticipantModel;
import com.hyrule.Backend.model.reports.ParticipantModelReport;
import com.hyrule.Backend.model.validate_registration.ValidateRegistrationModel;

/**
 * Creador de reportes de participantes en formato HTML
 */
public class ParticipantReportCreator {

    ValidationArchive validator = ValidationArchive.getInstance();

    public boolean obtenerDatosReporte(String codigoEvento, String tipoParticipante, String institucion,
            Path directoryPath) {

        if (!filtroCompleto(codigoEvento, tipoParticipante, institucion).isEmpty()) {
            crearReporteHTML(filtroCompleto(codigoEvento, tipoParticipante, institucion), directoryPath);
            return true;
        } else {

            return false;
        }

    }

    private Set<ParticipantModelReport> filtroCompleto(String codigoEvento, String tipoParticipante,
            String institucion) {

        Set<ParticipantModelReport> result = new HashSet<>();

        for (RegistrationModel reg : validator.getInscripciones()) {

            if (!reg.getCodigoEvento().equals(codigoEvento))
                continue;

            for (ParticipantModel part : validator.getParticipantes()) {

                if (part.getCorreoParticipante().equals(reg.getCorreoParticipante())) {

                    boolean tipoValido = tipoParticipante == null || tipoParticipante.isBlank()
                            || part.getTipoParticipante().toString().equals(tipoParticipante);

                    boolean institucionValida = institucion == null || institucion.isBlank()
                            || part.getInstitucion().equals(institucion);

                    if (!(tipoValido && institucionValida)) {
                        continue;
                    }
                }

                boolean isValid = false;
                for (ValidateRegistrationModel val : validator.getValidarInscripciones()) {

                    if (val.getCodigoEvento().equals(codigoEvento)
                            && val.getCorreo().equals(reg.getCorreoParticipante())) {
                        isValid = true;
                        break;

                    }

                }
                ParticipantModelReport report = new ParticipantModelReport(
                        reg.getCorreoParticipante(),
                        part.getTipoParticipante().toString(),
                        part.getInstitucion(),
                        part.getNombreCompleto(),
                        isValid);

                result.add(report);
            }
        }
        return result;

    }

    public void crearReporteHTML(Set<ParticipantModelReport> participantes, Path directoryPath) {

        Path salida = directoryPath.resolve("reporte_participantes"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html");

        List<ParticipantModelReport> lista = participantes.stream()
                .sorted(Comparator
                        .comparing(ParticipantModelReport::getInstitucion,
                                Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(ParticipantModelReport::getTipoParticipante,
                                Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(ParticipantModelReport::getNombreCompleto,
                                Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());

        long total = lista.size();
        long validados = lista.stream().filter(ParticipantModelReport::isValidado).count();

        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n")
                .append("<meta charset=\"UTF-8\">\n<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n")
                .append("<title>Reporte de Participantes</title>\n")
                .append("<style>\n")
                .append("body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Ubuntu,Arial,sans-serif;margin:24px;}\n")
                .append("h1{margin:0 0 8px 0;font-size:22px}\n")
                .append(".meta{color:#555;margin-bottom:16px}\n")
                .append(".kpi{display:flex;gap:16px;margin:12px 0 20px 0}\n")
                .append(".kpi div{background:#f6f6f6;padding:10px 12px;border-radius:10px}\n")
                .append("table{width:100%;border-collapse:collapse}\n")
                .append("th,td{border:1px solid #e2e2e2;padding:8px;text-align:left;font-size:14px}\n")
                .append("th{background:#fafafa}\n")
                .append("tr:nth-child(even){background:#fcfcfc}\n")
                .append(".no-validado{background:#fff1f0}\n")
                .append(".pill{display:inline-block;padding:2px 8px;border-radius:999px;border:1px solid #ddd;font-size:12px}\n")
                .append(".pill-ok{background:#f0fff4;border-color:#b7e4c7}\n")
                .append(".pill-bad{background:#fff5f5;border-color:#ffc9c9}\n")
                .append("</style>\n</head>\n<body>\n");

        html.append("<h1>Reporte de Participantes</h1>\n")
                .append("<div class=\"meta\">Generado: ").append(escapeHtml(fecha)).append("</div>\n")
                .append("<div class=\"kpi\">")
                .append("<div><strong>Total:</strong> ").append(total).append("</div>")
                .append("<div><strong>Validados:</strong> ").append(validados).append("</div>")
                .append("<div><strong>No validados:</strong> ").append(total - validados).append("</div>")
                .append("</div>\n");

        html.append("<table>\n<thead>\n<tr>")
                .append("<th>#</th>")
                .append("<th>Correo</th>")
                .append("<th>Nombre completo</th>")
                .append("<th>Tipo participante</th>")
                .append("<th>Institución</th>")
                .append("<th>Validado</th>")
                .append("</tr>\n</thead>\n<tbody>\n");

        int i = 1;
        for (ParticipantModelReport p : lista) {
            boolean ok = p.isValidado();
            html.append("<tr class=\"").append(ok ? "" : "no-validado").append("\">");
            html.append("<td>").append(i++).append("</td>");
            html.append("<td>").append(escapeHtml(nullToEmpty(p.getCorreoParticipante()))).append("</td>");
            html.append("<td>").append(escapeHtml(nullToEmpty(p.getNombreCompleto()))).append("</td>");
            html.append("<td>").append(escapeHtml(nullToEmpty(p.getTipoParticipante()))).append("</td>");
            html.append("<td>").append(escapeHtml(nullToEmpty(p.getInstitucion()))).append("</td>");
            html.append("<td>")
                    .append(ok ? "<span class=\"pill pill-ok\">Sí</span>" : "<span class=\"pill pill-bad\">No</span>")
                    .append("</td>");
            html.append("</tr>\n");
        }

        html.append("</tbody>\n</table>\n</body>\n</html>");

        // 4) Escribir archivo
        try (BufferedWriter bw = Files.newBufferedWriter(salida, StandardCharsets.UTF_8)) {
            bw.write(html.toString());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el reporte HTML: " + e.getMessage(), e);
        }

        System.out.println("Reporte generado en: " + salida.toAbsolutePath());
    }

    // ==== Helpers ====
    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

}
