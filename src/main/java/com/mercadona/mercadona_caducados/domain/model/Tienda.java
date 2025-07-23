package com.mercadona.mercadona_caducados.domain.model;

public class Tienda {

    private Integer id;
    private String nombre;
    private String ubicacion;
    private String passwordHashed;

    public Tienda(Integer id, String nombre, String ubicacion, String passwordHashed) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.passwordHashed = passwordHashed;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getPasswordHashed() {
        return passwordHashed;
    }

    public void setPasswordHashed(String passwordHashed) {
        this.passwordHashed = passwordHashed;
    }
}
