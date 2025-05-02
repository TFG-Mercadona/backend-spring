package com.mercadona.mercadona_caducados.domain.model;

public class Producto {
    private static final String IMAGEN_POR_DEFECTO = "/images/productos/notFound.jpg";

    private final int codigo;
    private final String nombre;
    private final String familia;
    private final int caducidadDias;
    private final String imagenUrl;

    public Producto(int codigo, String nombre, String familia, int caducidadDias, String imagenUrl) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.familia = familia;
        this.caducidadDias = caducidadDias;
        this.imagenUrl = (imagenUrl == null || imagenUrl.isBlank()) ? IMAGEN_POR_DEFECTO : imagenUrl;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFamilia() {
        return familia;
    }

    public int getCaducidadDias() {
        return caducidadDias;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }
}
