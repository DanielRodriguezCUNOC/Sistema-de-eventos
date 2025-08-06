package com.hyrule.Backend.persistence.event;

import java.time.LocalDate;
import com.hyrule.Backend.model.event.EventModel;
import com.hyrule.Backend.model.event.EventType;

public class EventRegisterData {
        String codigo;
        String fechaStr;
        String tipoStr;
        String titulo;
        String ubicacion;
        int cupoMax;

    public EventRegisterData(String codigo, String fechaStr, String tipoStr, String titulo, String ubicacion, int cupoMax) {
        this.codigo = codigo;
        this.fechaStr = fechaStr;
        this.tipoStr = tipoStr;
        this.titulo = titulo;
        this.ubicacion = ubicacion;
        this.cupoMax = cupoMax;
    }

    public boolean registrarEvento(){
        try{
            LocalDate fecha = LocalDate.parse(fechaStr);
            EventType tipo = EventType.valueOf(tipoStr.toUpperCase()); //* toUpperCase sirve para asegurar que el tipo coincida con los valores del enum

            //*Creamos un objeto que representa la tabla en la BD */
            EventModel event = new EventModel(codigo, fecha, tipo, titulo, ubicacion, cupoMax);

            //*Enviamos los datos para su insercion en la BD */
            ControlEvent controlEvent = new ControlEvent();
            controlEvent.insert(event);
            

        }catch (Exception e) {
            System.out.println("Error al registrar el evento: " + e.getMessage());
        }
        return false; //* Retorna false si no se pudo registrar el evento
    }

}

