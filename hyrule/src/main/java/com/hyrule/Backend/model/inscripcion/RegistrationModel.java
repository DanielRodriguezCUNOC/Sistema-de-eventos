package com.hyrule.Backend.model.inscripcion;

/**
 * Modelo de datos para inscripciones a eventos
 */
public class RegistrationModel {
    private String correoParticipante;
    private String codigoEvento;
    private RegistrationType tipoInscripcion;

    public RegistrationModel(String correoParticipante, String codigoEvento, RegistrationType tipoInscripcion) {
        this.correoParticipante = correoParticipante;
        this.codigoEvento = codigoEvento;
        this.tipoInscripcion = tipoInscripcion;
    }

    public RegistrationModel(String correoParticipante, String codigoEvento, String tipoInscripcion) {
        this(correoParticipante, codigoEvento, RegistrationType.valueOf(tipoInscripcion.toUpperCase()));
    }

    // * Getters y Setters */
    public String getCorreoParticipante() {
        return correoParticipante;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public RegistrationType getTipoInscripcion() {
        return tipoInscripcion;
    }

    public void setCorreoParticipante(String correoParticipante) {
        this.correoParticipante = correoParticipante;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public void setTipoInscripcion(RegistrationType tipoInscripcion) {
        this.tipoInscripcion = tipoInscripcion;
    }

    public void setTipoInscripcion(String tipoInscripcion) {
        this.tipoInscripcion = RegistrationType.valueOf(tipoInscripcion.toUpperCase());
    }

    @Override
    public String toString() {
        return "RegistrationModel{" +
                "correoParticipante='" + correoParticipante + '\'' +
                ", codigoEvento='" + codigoEvento + '\'' +
                ", tipoInscripcion=" + tipoInscripcion +
                '}';
    }
}
