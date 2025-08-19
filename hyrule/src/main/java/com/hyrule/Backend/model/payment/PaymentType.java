package com.hyrule.Backend.model.payment;

/**
 * Tipos de pago disponibles en el sistema
 */
public enum PaymentType {
    EFECTIVO,
    TRANSFERENCIA,
    TARJETA;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
