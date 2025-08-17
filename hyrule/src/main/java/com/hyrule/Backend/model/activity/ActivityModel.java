package com.hyrule.Backend.model.activity;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ActivityModel {

    private String codigoActividad;
    private String codigoEvento;
    private ActivityType tipoActividad;
    private String tituloActividad;
    private String correo;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer cupoMaximo;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public ActivityModel(String codigoActividad, String codigoEvento, ActivityType tipoActividad,
            String tituloActividad,
            String correo, LocalTime horaInicio, LocalTime horaFin, Integer cupoMaximo) {
        this.codigoActividad = codigoActividad;
        this.codigoEvento = codigoEvento;
        this.tipoActividad = tipoActividad;
        this.tituloActividad = tituloActividad;
        this.correo = correo;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.cupoMaximo = cupoMaximo;
    }

    public ActivityModel(String codigoActividad, String codigoEvento, String tipoActividadStr, String tituloActividad,
            String correo, String horaInicio, String horaFin, int cupoMaximo) {

        this.codigoActividad = codigoActividad;
        this.codigoEvento = codigoEvento;
        this.tipoActividad = ActivityType.valueOf(tipoActividadStr);
        this.tituloActividad = tituloActividad;
        this.correo = correo;
        this.horaInicio = LocalTime.parse(horaInicio, TIME_FORMAT);
        this.horaFin = LocalTime.parse(horaFin, TIME_FORMAT);
        this.cupoMaximo = cupoMaximo;
    }

    // Getters y Setters
    public String getCodigoActividad() {
        return codigoActividad;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public ActivityType getTipoActividad() {
        return tipoActividad;
    }

    public String getTituloActividad() {
        return tituloActividad;
    }

    public String getCorreo() {
        return correo;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCodigoActividad(String codigoActividad) {
        this.codigoActividad = codigoActividad;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public void setTipoActividad(ActivityType tipoActividad) {
        this.tipoActividad = tipoActividad;

    }

    public void setTituloActividad(String tituloActividad) {
        this.tituloActividad = tituloActividad;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    @Override
    public String toString() {
        return "ActivityModel{" +
                "codigoActividad='" + codigoActividad + '\'' +
                ", codigoEvento='" + codigoEvento + '\'' +
                ", tipoActividad='" + tipoActividad + '\'' +
                ", tituloActividad='" + tituloActividad + '\'' +
                ", correo='" + correo + '\'' +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", cupoMaximo=" + cupoMaximo +
                '}';
    }
}
