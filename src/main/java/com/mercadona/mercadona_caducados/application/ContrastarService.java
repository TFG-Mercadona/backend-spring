// application/ContrastarService.java
package com.mercadona.mercadona_caducados.application;

import com.mercadona.mercadona_caducados.application.dto.CambioCaducidadDTO;
import com.mercadona.mercadona_caducados.application.dto.CambioResumenDTO;
import com.mercadona.mercadona_caducados.infrastructure.persistence.SpringDataCambioCaducidadRepository;
import com.mercadona.mercadona_caducados.infrastructure.persistence.SpringDataProductoRepository;
import com.mercadona.mercadona_caducados.infrastructure.persistence.SpringDataTornilloRepository;
import com.mercadona.mercadona_caducados.infrastructure.persistence.TornilloCambioCaducidadEntity;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContrastarService {

    private final SpringDataCambioCaducidadRepository cambiosRepo;
    private final SpringDataTornilloRepository springTornillos;
    private final SpringDataProductoRepository productoRepo;

    public ContrastarService(SpringDataCambioCaducidadRepository cambiosRepo,
                             SpringDataTornilloRepository springTornillos,
                             SpringDataProductoRepository productoRepo) {
        this.cambiosRepo = cambiosRepo;
        this.springTornillos = springTornillos;
        this.productoRepo = productoRepo;
    }

    /** Cambios de hoy para una familia concreta, enriquecidos con nombre/imagen/caducidadDias */
    public List<CambioCaducidadDTO> cambiosDeHoy(Integer tiendaId, String familia) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime desde = hoy.atStartOfDay();
        LocalDateTime hasta = hoy.plusDays(1).atStartOfDay().minusNanos(1);

        var rows = cambiosRepo.findCambiosDeRango(tiendaId, familia, desde, hasta);

        return rows.stream().map(c -> {
            String nombre = null, imagenUrl = null;
            Integer caducidadDias = null;

            springTornillos.findDTOByTiendaIdAndProductoCodigo(c.getTiendaId(), c.getProductoCodigo())
                    .ifPresent(d -> {
                        // d es tu proyección: d.nombre, d.imagenUrl, d.caducidadDias
                        // (campos públicos en la proyección DTO)
                        // Nota: si son privados con getters, usa getters.
                        // Aquí asumo públicos según tu código anterior.
                    });

            var dOpt = springTornillos.findDTOByTiendaIdAndProductoCodigo(c.getTiendaId(), c.getProductoCodigo());
            if (dOpt.isPresent()) {
                var d = dOpt.get();
                nombre = d.nombre;
                imagenUrl = d.imagenUrl;
                caducidadDias = d.caducidadDias;
            }

            return new CambioCaducidadDTO(
                    c.getId(), c.getTornilloId(), c.getProductoCodigo(), c.getTiendaId(),
                    c.getFamilia(), c.getNombreModulo(), c.getFila(), c.getColumna(),
                    nombre, imagenUrl,
                    c.getFechaAnterior(), c.getFechaNueva(),
                    c.getFechaCambio(), caducidadDias
            );
        }).toList();
    }

    /** Resumen de hoy (todas las familias o filtrado por ?familia=...), con flag contrastado */
    public List<CambioResumenDTO> resumenHoy(Integer tiendaId, String familia) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime desde = hoy.atStartOfDay();
        LocalDateTime hasta = hoy.atTime(23, 59, 59, 999_000_000);

        List<TornilloCambioCaducidadEntity> rows =
                cambiosRepo.findCambiosDeRango(tiendaId, familia, desde, hasta);

        return rows.stream().map(c -> {
            CambioResumenDTO dto = new CambioResumenDTO();
            dto.tornilloId     = c.getTornilloId();
            dto.tiendaId       = c.getTiendaId();
            dto.productoCodigo = c.getProductoCodigo();
            dto.familia        = c.getFamilia();
            dto.nombreModulo   = c.getNombreModulo();
            dto.fila           = c.getFila();
            dto.columna        = c.getColumna();
            dto.fechaAnterior  = c.getFechaAnterior() != null ? c.getFechaAnterior().toString() : null;
            dto.fechaNueva     = c.getFechaNueva()    != null ? c.getFechaNueva().toString()    : null;
            dto.contrastado    = c.isContrastado();

            productoRepo.findById(c.getProductoCodigo()).ifPresent(p -> {
                dto.nombre    = p.getNombre();
                dto.imagenUrl = p.getImagenUrl();
            });

            return dto;
        }).toList();
    }

    /** Marca/unmarca un cambio como contrastado */
    @Transactional
    public void marcarContrastado(Long cambioId, boolean valor) {
        int updated = cambiosRepo.marcarContrastado(cambioId, valor);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cambio no encontrado: " + cambioId);
        }
    }
}
