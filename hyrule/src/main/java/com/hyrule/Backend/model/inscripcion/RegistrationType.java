package com.hyrule.Backend.model.inscripcion;

public enum RegistrationType {
    ASISTENTE,
    CONFERENCISTA,
    TALLERISTA,
    OTRO;

    @Override
    public String toString() {
        return name().toUpperCase();
    }
}
