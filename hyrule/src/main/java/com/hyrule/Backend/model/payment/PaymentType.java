package com.hyrule.Backend.model.payment;

public enum PaymentType {
    EFECTIVO,
    TRANSFERENCIA,
    TARJETA;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
