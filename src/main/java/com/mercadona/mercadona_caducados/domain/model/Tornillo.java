package com.mercadona.mercadona_caducados.domain.model;

import java.time.LocalDate;

public class Tornillo {

    private final Long id;
    private final Integer productoCodigo;
    private final Integer tiendaId;
    private final LocalDate fechaCaducidad;
    private final LocalDate fechaRetirada;
    private final String nombreModulo;
    private final Integer fila;
    private final Integer columna;

    public Tornillo(Long id, Integer productoCodigo, Integer tiendaId, LocalDate fechaCaducidad,
                    LocalDate fechaRetirada, String nombreModulo, Integer fila, Integer columna) {
        this.id = id;
        this.productoCodigo = productoCodigo;
        this.tiendaId = tiendaId;
        this.fechaCaducidad = fechaCaducidad;
        this.fechaRetirada = fechaRetirada;
        this.nombreModulo = nombreModulo;
        this.fila = fila;
        this.columna = columna;
    }

    public Long getId() {
        return id;
    }

    public Integer getProductoCodigo() {
        return productoCodigo;
    }

    public Integer getTiendaId() {
        return tiendaId;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public LocalDate getFechaRetirada() {
        return fechaRetirada;
    }

    public String getNombreModulo() {
        return nombreModulo;
    }

    public Integer getFila() {
        return fila;
    }

    public Integer getColumna() {
        return columna;
    }
}
