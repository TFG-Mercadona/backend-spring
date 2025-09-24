package com.mercadona.mercadona_caducados.application.dto;

public class CambioResumenDTO {
    public Long   tornilloId;
    public Integer tiendaId;
    public Integer productoCodigo;
    public String  familia;         // opcional
    public String  nombreModulo;    // opcional
    public Integer fila;            // opcional
    public Integer columna;         // opcional
    public String  fechaAnterior;   // "YYYY-MM-DD" o null
    public String  fechaNueva;      // "YYYY-MM-DD" o null
    public Boolean contrastado;     // true/false
    public String  nombre;          // opcional
    public String  imagenUrl;       // opcional

    public CambioResumenDTO() {}
}