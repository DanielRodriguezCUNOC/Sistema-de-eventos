package com.hyrule.Backend.model.participant;

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

    // * Getters y Setters */
    public String getCorreo_participante() {
        return correo_participante;
    }

    public String getNombre_completo() {
        return nombre_completo;
    }

    public ParticipantType getTipo_participante() {
        return tipo_participante;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setCorreo_participante(String correo_participante) {
        this.correo_participante = correo_participante;
    }

    public void setNombre_completo(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public void setTipo_participante(ParticipantType tipo_participante) {
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
