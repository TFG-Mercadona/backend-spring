package com.mercadona.mercadona_caducados.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "tienda")
public class JpaTiendaEntity {

    @Id
    private Integer id;

    private String nombre;
    private String ubicacion;

    @Column(name = "password")
    private String passwordHashed;

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public String getPasswordHashed() { return passwordHashed; }
}
