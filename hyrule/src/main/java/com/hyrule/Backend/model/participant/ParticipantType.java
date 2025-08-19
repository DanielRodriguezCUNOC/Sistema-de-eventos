package com.hyrule.Backend.model.participant;

/**
 * Tipos de participante en eventos
 */
public enum ParticipantType {
    ESTUDIANTE,
    PROFESIONAL,
    INVITADO;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
