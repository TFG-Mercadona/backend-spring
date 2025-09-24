package com.mercadona.mercadona_caducados.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tornillo_cambio_caducidad")
public class TornilloCambioCaducidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tornilloId;
    private Integer tiendaId;
    private Integer productoCodigo;

    // Si no tienes familia en TornilloEntity, puedes dejarla nullable
    private String familia;

    private String nombreModulo;
    private Integer fila;
    private Integer columna;

    private LocalDate fechaAnterior;
    private LocalDate fechaNueva;

    private Integer caducidadDias;

    private LocalDateTime fechaCambio;

    private boolean contrastado = false;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTornilloId() { return tornilloId; }
    public void setTornilloId(Long tornilloId) { this.tornilloId = tornilloId; }

    public Integer getTiendaId() { return tiendaId; }
    public void setTiendaId(Integer tiendaId) { this.tiendaId = tiendaId; }

    public Integer getProductoCodigo() { return productoCodigo; }
    public void setProductoCodigo(Integer productoCodigo) { this.productoCodigo = productoCodigo; }

    public String getFamilia() { return familia; }
    public void setFamilia(String familia) { this.familia = familia; }

    public String getNombreModulo() { return nombreModulo; }
    public void setNombreModulo(String nombreModulo) { this.nombreModulo = nombreModulo; }

    public Integer getFila() { return fila; }
    public void setFila(Integer fila) { this.fila = fila; }

    public Integer getColumna() { return columna; }
    public void setColumna(Integer columna) { this.columna = columna; }

    public LocalDate getFechaAnterior() { return fechaAnterior; }
    public void setFechaAnterior(LocalDate fechaAnterior) { this.fechaAnterior = fechaAnterior; }

    public LocalDate getFechaNueva() { return fechaNueva; }
    public void setFechaNueva(LocalDate fechaNueva) { this.fechaNueva = fechaNueva; }

    public Integer getCaducidadDias() { return caducidadDias; }
    public void setCaducidadDias(Integer caducidadDias) { this.caducidadDias = caducidadDias; }

    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }

    public boolean isContrastado() { return contrastado; }
    public void setContrastado(boolean contrastado) { this.contrastado = contrastado;  }
}