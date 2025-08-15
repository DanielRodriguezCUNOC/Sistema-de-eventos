package com.hyrule.Backend.model.asistencia;

public class AttendanceModel {
    private String codigoActividad;
    private String correoParticipante;

    public AttendanceModel(String codigoActividad, String correoParticipante) {
        this.codigoActividad = codigoActividad;
        this.correoParticipante = correoParticipante;
    }

    public AttendanceModel() {
    }

    public String getCodigoActividad() {
        return codigoActividad;
    }

    public void setCodigoActividad(String codigoActividad) {
        this.codigoActividad = codigoActividad;
    }

    public String getCorreoParticipante() {
        return correoParticipante;
    }

    public void setCorreoParticipante(String correoParticipante) {
        this.correoParticipante = correoParticipante;
    }
}
