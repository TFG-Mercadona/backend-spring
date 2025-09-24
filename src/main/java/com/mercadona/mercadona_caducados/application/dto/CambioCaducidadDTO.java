package com.mercadona.mercadona_caducados.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CambioCaducidadDTO {
  public Long id;            // id del cambio
  public Long tornilloId;
  public Integer productoCodigo;
  public Integer tiendaId;
  public String familia;
  public String nombreModulo;
  public Integer fila;
  public Integer columna;
  public String nombre;      // del producto (viene de tu proyección TornilloConProductoDTO)
  public String imagenUrl;   // idem
  public LocalDate fechaAnterior;
  public LocalDate fechaNueva;
  public LocalDateTime fechaCambio;
  public Integer caducidadDias;

  public CambioCaducidadDTO() {}
  public CambioCaducidadDTO(
      Long id, Long tornilloId, Integer productoCodigo, Integer tiendaId,
      String familia, String nombreModulo, Integer fila, Integer columna,
      String nombre, String imagenUrl, LocalDate fechaAnterior, LocalDate fechaNueva,
      LocalDateTime fechaCambio, Integer caducidadDias) {
    this.id = id; this.tornilloId = tornilloId; this.productoCodigo = productoCodigo;
    this.tiendaId = tiendaId; this.familia = familia; this.nombreModulo = nombreModulo;
    this.fila = fila; this.columna = columna; this.nombre = nombre; this.imagenUrl = imagenUrl;
    this.fechaAnterior = fechaAnterior; this.fechaNueva = fechaNueva;
    this.fechaCambio = fechaCambio; this.caducidadDias = caducidadDias;
  }
}