package com.hyrule.Backend.model.certified;

public class CertifiedModel {

    private String codigoEvento;
    private String correoParticipante;

    public CertifiedModel(String codigoEvento, String correoParticipante) {
        this.codigoEvento = codigoEvento;
        this.correoParticipante = correoParticipante;
    }

    public CertifiedModel() {
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public String getCorreoParticipante() {
        return correoParticipante;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public void setCorreoParticipante(String correoParticipante) {
        this.correoParticipante = correoParticipante;
    }
}
