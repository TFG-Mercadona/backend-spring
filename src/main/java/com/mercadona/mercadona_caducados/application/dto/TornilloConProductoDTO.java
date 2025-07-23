package com.mercadona.mercadona_caducados.application.dto;

public class TornilloConProductoDTO {
    public Long id;
    public Integer productoCodigo;
    public Integer tiendaId;
    public String fechaCaducidad;
    public String fechaRetirada;
    public String nombreModulo;
    public Integer fila;
    public Integer columna;
    public String nombre;
    public String imagenUrl;

    public TornilloConProductoDTO(Long id, Integer productoCodigo, Integer tiendaId, String fechaCaducidad, String fechaRetirada,
                                  String nombreModulo, Integer fila, Integer columna, String nombre, String imagenUrl) {
        this.id = id;
        this.productoCodigo = productoCodigo;
        this.tiendaId = tiendaId;
        this.fechaCaducidad = fechaCaducidad;
        this.fechaRetirada = fechaRetirada;
        this.nombreModulo = nombreModulo;
        this.fila = fila;
        this.columna = columna;
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
    }
}
