package com.hyrule.Backend.model.event;

public enum EventType {
    CHARLA, CONGRESO, TALLER, DEBATE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
