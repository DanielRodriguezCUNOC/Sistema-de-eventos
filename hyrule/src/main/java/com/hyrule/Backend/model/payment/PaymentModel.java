package com.hyrule.Backend.model.payment;

import java.math.BigDecimal;

public class PaymentModel {

    private String codigoEvento;
    private String correo;
    private PaymentType tipoPago;
    private BigDecimal monto;

    public PaymentModel(String codigoEvento, String correo, PaymentType tipoPago, BigDecimal monto) {
        this.codigoEvento = codigoEvento;
        this.correo = correo;
        this.tipoPago = tipoPago;
        this.monto = monto;
    }

    public PaymentModel(String codigoEvento, String correo, String tipoPagoStr, String montoStr) {
        this.codigoEvento = codigoEvento;
        this.correo = correo;
        this.tipoPago = PaymentType.valueOf(tipoPagoStr.toUpperCase());
        this.monto = new BigDecimal(montoStr);
    }

    public PaymentModel() {
    }

    // *Getters y Setters*/

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public PaymentType getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(PaymentType tipoPago) {
        this.tipoPago = tipoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    @Override
    public String toString() {
        return "PaymentModel{" +
                "codigoEvento='" + codigoEvento + '\'' +
                ", correo='" + correo + '\'' +
                ", tipoPago=" + tipoPago +
                ", monto=" + monto +
                '}';
    }
}