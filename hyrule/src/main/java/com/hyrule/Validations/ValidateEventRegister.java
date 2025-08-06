package com.hyrule.Validations;

public class ValidateEventRegister {
   
        String codigo;
        String fechaStr;
        String tipoStr;
        String titulo;
        String ubicacion;
        int cupoMax;
    


    public ValidateEventRegister(String codigo, String fechaStr, String tipoStr, String titulo, String ubicacion, int cupoMax) {
        this.codigo = codigo;
        this.fechaStr = fechaStr;
        this.tipoStr = tipoStr;
        this.titulo = titulo;
        this.ubicacion = ubicacion;
        this.cupoMax = cupoMax;

    }

    public boolean isValid() {
        return !codigo.isEmpty() && !fechaStr.isEmpty() && !tipoStr.isEmpty() && !titulo.isEmpty() && !ubicacion.isEmpty() && cupoMax > 0;
    }

}
