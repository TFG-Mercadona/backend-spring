package com.mercadona.mercadona_caducados.application.dto;

public class CreateTornilloRequest {
    private Integer tiendaId;         // obligatorio
    private Integer productoCodigo;   // obligatorio
    private String  nombreModulo;     // obligatorio
    private Integer fila;             // obligatorio
    private Integer columna;          // obligatorio

    // Opcionales:
    private String  fechaCaducidad;   // "YYYY-MM-DD" (opcional)
    private Integer caducidadDias;    // opcional (si viene y hay fechaCad, calculamos retirada = cad - dias)

    public Integer getTiendaId() { return tiendaId; }
    public void setTiendaId(Integer tiendaId) { this.tiendaId = tiendaId; }

    public Integer getProductoCodigo() { return productoCodigo; }
    public void setProductoCodigo(Integer productoCodigo) { this.productoCodigo = productoCodigo; }

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
