package com.hyrule.Backend.model.participant;

/**
 * Modelo de datos para participantes en eventos
 */
public class ParticipantModel {

    private String correo_participante;
    private String nombre_completo;
    private ParticipantType tipo_participante;
    private String institucion;

    public ParticipantModel(String correo_participante, String nombre_completo, ParticipantType tipo_participante,
            String institucion) {
        this.correo_participante = correo_participante;
        this.nombre_completo = nombre_completo;
        this.tipo_participante = tipo_participante;
        this.institucion = institucion;
    }

    public ParticipantModel(String correo_participante, String nombre_completo,
            String tipo_participante, String institucion) {
        this.correo_participante = correo_participante;
        this.nombre_completo = nombre_completo;
        this.tipo_participante = ParticipantType.valueOf(tipo_participante.toUpperCase());
        this.institucion = institucion;
    }

    // * Getters y Setters */
    public String getCorreoParticipante() {
        return correo_participante;
    }

    public String getNombreCompleto() {
        return nombre_completo;
    }

    public ParticipantType getTipoParticipante() {
        return tipo_participante;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setCorreoParticipante(String correo_participante) {
        this.correo_participante = correo_participante;
    }

    public void setNombreCompleto(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public void setTipoParticipante(ParticipantType tipo_participante) {
        this.tipo_participante = tipo_participante;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    // *Funcion toString */
    @Override
    public String toString() {
        return "ParticipantModel{" +
                "correo_participante='" + correo_participante + '\'' +
                ", nombre_completo='" + nombre_completo + '\'' +
                ", tipo_participante=" + tipo_participante +
                ", institucion='" + institucion + '\'' +
                '}';
    }
}
