package com.hyrule.Backend.model.participant;

public enum ParticipantType {
    ESTUDIANTE,
    PROFESIONAL,
    INVITADO;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
