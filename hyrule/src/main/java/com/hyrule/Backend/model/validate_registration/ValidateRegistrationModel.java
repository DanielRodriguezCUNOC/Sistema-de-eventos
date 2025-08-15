package com.hyrule.Backend.model.validate_registration;

public class ValidateRegistrationModel {
    private String correo;
    private String codigoEvento;

    public ValidateRegistrationModel(String correo, String codigoEvento) {
        this.correo = correo;
        this.codigoEvento = codigoEvento;
    }

    public String getCorreo() {
        return correo;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }
}
