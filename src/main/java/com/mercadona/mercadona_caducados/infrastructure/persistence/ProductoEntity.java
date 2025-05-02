package com.mercadona.mercadona_caducados.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto")
public class ProductoEntity {

    @Id
    private int codigo;

    private String nombre;
    private String familia;
    private int caducidadDias;
    private String imagenUrl;

    public ProductoEntity() {
        // constructor por defecto necesario para JPA
    }

    public ProductoEntity(int codigo, String nombre, String familia, int caducidadDias, String imagenUrl) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.familia = familia;
        this.caducidadDias = caducidadDias;
        this.imagenUrl = imagenUrl;
    }

    // Getters y setters...
    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFamilia() { return familia; }
    public void setFamilia(String familia) { this.familia = familia; }

    public int getCaducidadDias() { return caducidadDias; }
    public void setCaducidadDias(int caducidadDias) { this.caducidadDias = caducidadDias; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}
