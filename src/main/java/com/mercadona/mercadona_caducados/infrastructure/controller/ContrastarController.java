// infrastructure/controller/ContrastarController.java
package com.mercadona.mercadona_caducados.infrastructure.controller;

import com.mercadona.mercadona_caducados.application.ContrastarService;
import com.mercadona.mercadona_caducados.application.dto.CambioCaducidadDTO;
import com.mercadona.mercadona_caducados.application.dto.CambioResumenDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contrastar")
public class ContrastarController {

    private final ContrastarService service;

    public ContrastarController(ContrastarService service) {
        this.service = service;
    }

    @GetMapping("/tienda/{tiendaId}/familia/{familia}/hoy")
    public List<CambioCaducidadDTO> cambiosDeHoyPorFamilia(
            @PathVariable Integer tiendaId,
            @PathVariable String familia
    ) {
        return service.cambiosDeHoy(tiendaId, familia);
    }

    // /api/contrastar/tienda/{tiendaId}/hoy?familia=Charcutería
    @GetMapping("/tienda/{tiendaId}/hoy")
    public List<CambioResumenDTO> resumenHoy(
            @PathVariable Integer tiendaId,
            @RequestParam(value = "familia", required = false) String familia
    ) {
        return service.resumenHoy(tiendaId, familia);
    }

    @PutMapping("/{cambioId}/contrastado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarContrastado(
            @PathVariable Long cambioId,
            @RequestBody MarcarContrastadoRequest req
    ) {
        if (req == null || req.contrastado == null) {
            throw new IllegalArgumentException("Body requerido: { \"contrastado\": true|false }");
        }
        service.marcarContrastado(cambioId, Boolean.TRUE.equals(req.contrastado));
    }

    public static class MarcarContrastadoRequest {
        public Boolean contrastado;
    }
}
