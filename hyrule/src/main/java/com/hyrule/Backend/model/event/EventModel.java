package com.hyrule.Backend.model.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EventModel {
    private String codigoEvento;
    private LocalDate fechaEvento;
    private EventType tipoEvento;
    private String tituloEvento;
    private String ubicacionEvento;
    private Integer cupoMaxParticipantes;
    private BigDecimal costoEvento;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EventModel(String codigoEvento, LocalDate fechaEvento, EventType tipoEvento, String tituloEvento,
            String ubicacionEvento, Integer cupoMaxParticipantes, BigDecimal costoEvento) {
        this.codigoEvento = codigoEvento;
        this.fechaEvento = fechaEvento;
        this.tipoEvento = tipoEvento;
        this.tituloEvento = tituloEvento;
        this.ubicacionEvento = ubicacionEvento;
        this.cupoMaxParticipantes = cupoMaxParticipantes;
        this.costoEvento = costoEvento;
    }

    public EventModel(String codigoEvento, String fechaEventoStr, String tipoEvento, String tituloEvento,
            String ubicacionEvento, Integer cupoMaxParticipantes, String costoEventoStr) {
        this.codigoEvento = codigoEvento;
        this.fechaEvento = LocalDate.parse(fechaEventoStr, formatter);
        this.tipoEvento = EventType.valueOf(tipoEvento.toUpperCase());
        this.tituloEvento = tituloEvento;
        this.ubicacionEvento = ubicacionEvento;
        this.cupoMaxParticipantes = cupoMaxParticipantes;
        this.costoEvento = new BigDecimal(costoEventoStr);
    }

    // * Getters y Setters */
    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public LocalDate getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDate fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public EventType getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(EventType tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getTituloEvento() {
        return tituloEvento;
    }

    public void setTituloEvento(String tituloEvento) {
        this.tituloEvento = tituloEvento;
    }

    public String getUbicacionEvento() {
        return ubicacionEvento;
    }

    public void setUbicacionEvento(String ubicacionEvento) {
        this.ubicacionEvento = ubicacionEvento;
    }

    public Integer getCupoMaxParticipantes() {
        return cupoMaxParticipantes;
    }

    public void setCupoMaxParticipantes(Integer cupoMaxParticipantes) {
        this.cupoMaxParticipantes = cupoMaxParticipantes;
    }

    public BigDecimal getCostoEvento() {
        return costoEvento;
    }

    public void setCostoEvento(BigDecimal costoEvento) {
        this.costoEvento = costoEvento;
    }

    // * Funcion toString para representar el evento como un String */
    @Override
    public String toString() {
        return "Evento{" +
                "codigoEvento='" + codigoEvento + '\'' +
                ", fechaEvento=" + fechaEvento +
                ", tipoEvento=" + tipoEvento +
                ", tituloEvento='" + tituloEvento + '\'' +
                ", ubicacionEvento='" + ubicacionEvento + '\'' +
                ", cupoMaxParticipantes=" + cupoMaxParticipantes +
                ", costoEvento=" + costoEvento +
                '}';
    }

}
