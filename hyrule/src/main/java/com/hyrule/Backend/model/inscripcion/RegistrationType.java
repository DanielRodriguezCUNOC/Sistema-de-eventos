package com.hyrule.Backend.model.inscripcion;

/**
 * Tipos de registro disponibles para inscripciones
 */
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
