package com.hyrule.Backend.model.participant;

public enum ParticipantType {
    ESTUDIANTE,
    PROFESOR,
    INVITADO;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
