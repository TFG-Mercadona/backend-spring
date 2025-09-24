package com.mercadona.mercadona_caducados.application.dto;

public class UpdateTornilloBasicoRequest {
    // Todos opcionales: solo se actualiza lo que venga
    private String nombreModulo;     // ej. "Puerta 1"
    private Integer fila;            // ej. 1..N
    private Integer columna;         // ej. 1..N
    private String fechaCaducidad;   // "YYYY-MM-DD"
    private Integer caducidadDias;   // para recalcular fechaRetirada si llega junto con fechaCaducidad

    public String getNombreModulo() { return nombreModulo; }
    public void setNombreModulo(String nombreModulo) { this.nombreModulo = nombreModulo; }

    public Integer getFila() { return fila; }
    public void setFila(Integer fila) { this.fila = fila; }

    public Integer getColumna() { return columna; }
    public void setColumna(Integer columna) { this.columna = columna; }

    public String getFechaCaducidad() { return fechaCaducidad; }
    public void setFechaCaducidad(String fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }

    public Integer getCaducidadDias() { return caducidadDias; }
    public void setCaducidadDias(Integer caducidadDias) { this.caducidadDias = caducidadDias; }
}