package com.hyrule.Backend.handler;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.Set;
import java.util.regex.Pattern;

import com.hyrule.Backend.LogFormatter;
import com.hyrule.Backend.RegularExpresion.RExpresionParticipantReport;
import com.hyrule.Backend.model.reports.ParticipantModelReport;
import com.hyrule.Backend.persistence.reports.ReportParticipantControl;
import com.hyrule.Backend.reports.ParticipantReportCreator;
import com.hyrule.interfaces.RegisterHandler;

public class ParticipantReportHandler implements RegisterHandler {

    private Path directoryPath;
    private Connection conn;

    ReportParticipantControl control = new ReportParticipantControl();

    /**
     * Constructor que recibe la conexion a la base de datos
     */
    public ParticipantReportHandler(Connection conn) {
        this.conn = conn;
    }

    /**
     * Método para procesar una línea de reporte.
     * 
     * @param linea     línea del archivo con datos del reporte
     * @param logWriter escritor para registrar errores y éxitos
     * @return true si el reporte se procesó correctamente
     */
    @Override
    public boolean process(String linea, LogFormatter logWriter) {

        try {
            RExpresionParticipantReport parser = new RExpresionParticipantReport();
            String[] datos = parser.parseParticipantReport(linea);
            if (datos == null) {
                logWriter.error("Línea inválida o incompleta: " + linea);
                return false;
            }

            ParticipantReportCreator reportCreator = new ParticipantReportCreator();
            if (!reportCreator.obtenerDatosReporte(datos[0], datos[1], datos[2], directoryPath)) {
                logWriter.error("Error al generar el reporte de participantes: " + linea);
                return false;
            }

            logWriter.info("Reporte de participantes procesado correctamente: " + linea);
            return true;
        } catch (Exception e) {
            try {
                logWriter.error("Error al procesar el reporte de participantes: " + e.getMessage());
            } catch (Exception ignore) {
            }
        }
        return false;
    }

    public boolean generarReporteForm(String codigoEvento, String tipoParticipante, String institucion) {
        try {
            Set<ParticipantModelReport> participantes;
            if (!"Ok".equals(validateForm(codigoEvento, tipoParticipante, institucion))) {
                return false;
            }
            participantes = control.obtenerDatosReporte(codigoEvento, tipoParticipante, institucion, conn);
            if (participantes != null && !participantes.isEmpty()) {

                ParticipantReportCreator reportCreator = new ParticipantReportCreator();

                reportCreator.crearReporteHTML(participantes, directoryPath);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String validateForm(String codigoEvento, String tipoParticipante, String institucion) {
        if (!validateDataIntegrity(codigoEvento, tipoParticipante, institucion)) {
            return "Datos del formulario inválidos.";
        }
        return "Ok";
    }

    public void setDirectoryPath(Path directoryPath) {
        this.directoryPath = directoryPath;
    }

    private boolean validateDataIntegrity(String codigoEvento, String tipoParticipante,
            String institucion) {

        boolean codigoEventoValido = codigoEvento != null &&
                codigoEvento.matches("^EVT-\\d{8}$") && !codigoEvento.isBlank();

        boolean isValidType = Pattern.matches(
                "^(ESTUDIANTE|PROFESIONAL|INVITADO)$", tipoParticipante)
                && tipoParticipante != null;

        boolean isValidInstitution = Pattern.matches(
                "^[\\p{L}\\p{N}\\-\\s]{1,150}$", institucion)
                && institucion != null && !institucion.isBlank();

        return codigoEventoValido && isValidType && isValidInstitution;

    }

}
