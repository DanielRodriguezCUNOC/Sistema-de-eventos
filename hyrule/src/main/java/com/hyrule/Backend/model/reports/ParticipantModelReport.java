package com.hyrule.Backend.model.reports;

/**
 * Modelo de datos para reportes de participantes
 */
public class ParticipantModelReport {
    private String correoParticipante;
    String tipoParticipante;
    private String institucion;
    private String nombreCompleto;
    private boolean validado;

    public ParticipantModelReport(String correoParticipante, String tipoParticipante, String institucion,
            String nombreCompleto, boolean validado) {
        this.correoParticipante = correoParticipante;
        this.tipoParticipante = tipoParticipante;
        this.institucion = institucion;
        this.nombreCompleto = nombreCompleto;
        this.validado = validado;
    }

    // * Getters y Setters */

    public String getCorreoParticipante() {
        return correoParticipante;
    }

    public void setCorreoParticipante(String correoParticipante) {
        this.correoParticipante = correoParticipante;
    }

    public String getTipoParticipante() {
        return tipoParticipante;
    }

    public void setTipoParticipante(String tipoParticipante) {
        this.tipoParticipante = tipoParticipante;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public boolean isValidado() {
        return validado;
    }

    public void setValidado(boolean validado) {
        this.validado = validado;
    }

}
