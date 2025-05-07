package com.mercadona.mercadona_caducados.infrastructure.persistence;

import com.mercadona.mercadona_caducados.domain.model.Tornillo;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tornillo", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"producto_codigo", "tienda_id"})
})
public class TornilloEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_codigo", nullable = false)
    private Integer productoCodigo;

    @Column(name = "tienda_id", nullable = false)
    private Integer tiendaId;

    @Column(name = "fecha_caducidad", nullable = false)
    private LocalDate fechaCaducidad;

    @Column(name = "fecha_retirada", nullable = false)
    private LocalDate fechaRetirada;

    @Column(name = "nombre_modulo")
    private String nombreModulo;

    private Integer fila;
    private Integer columna;

    public TornilloEntity() {
        // Constructor por defecto requerido por JPA
    }

    // Getters y setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getProductoCodigo() { return productoCodigo; }
    public void setProductoCodigo(Integer productoCodigo) { this.productoCodigo = productoCodigo; }

    public Integer getTiendaId() { return tiendaId; }
    public void setTiendaId(Integer tiendaId) { this.tiendaId = tiendaId; }

    public LocalDate getFechaCaducidad() { return fechaCaducidad; }
    public void setFechaCaducidad(LocalDate fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }

    public LocalDate getFechaRetirada() { return fechaRetirada; }
    public void setFechaRetirada(LocalDate fechaRetirada) { this.fechaRetirada = fechaRetirada; }

    public String getNombreModulo() { return nombreModulo; }
    public void setNombreModulo(String nombreModulo) { this.nombreModulo = nombreModulo; }

    public Integer getFila() { return fila; }
    public void setFila(Integer fila) { this.fila = fila; }

    public Integer getColumna() { return columna; }
    public void setColumna(Integer columna) { this.columna = columna; }

    // Conversión a dominio
    public Tornillo toDomain() {
        return new Tornillo(
            id,
            productoCodigo,
            tiendaId,
            fechaCaducidad,
            fechaRetirada,
            nombreModulo,
            fila,
            columna
        );
    }

    // Conversión desde dominio
    public static TornilloEntity fromDomain(Tornillo tornillo) {
        TornilloEntity entity = new TornilloEntity();
        entity.id = tornillo.getId(); // se puede omitir si lo gestiona la BBDD
        entity.productoCodigo = tornillo.getProductoCodigo();
        entity.tiendaId = tornillo.getTiendaId();
        entity.fechaCaducidad = tornillo.getFechaCaducidad();
        entity.fechaRetirada = tornillo.getFechaRetirada();
        entity.nombreModulo = tornillo.getNombreModulo();
        entity.fila = tornillo.getFila();
        entity.columna = tornillo.getColumna();
        return entity;
    }
}
