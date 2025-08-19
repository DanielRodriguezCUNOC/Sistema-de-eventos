package com.hyrule.Backend.model.event;

/**
 * Tipos de evento disponibles en el sistema
 */
public enum EventType {
    CHARLA, CONGRESO, TALLER, DEBATE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
