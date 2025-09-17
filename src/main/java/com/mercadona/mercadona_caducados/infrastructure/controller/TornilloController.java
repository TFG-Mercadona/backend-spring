package com.mercadona.mercadona_caducados.infrastructure.controller;

import com.mercadona.mercadona_caducados.application.TornilloService;
import com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO;
import com.mercadona.mercadona_caducados.domain.model.Tornillo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tornillos")
public class TornilloController {

    private final TornilloService tornilloService;

    public TornilloController(TornilloService tornilloService) {
        this.tornilloService = tornilloService;
    }

    @GetMapping
    public List<Tornillo> obtenerTodos() {
        return tornilloService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Tornillo obtenerPorId(@PathVariable Long id) {
        return tornilloService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Tornillo no encontrado"));
    }

    @GetMapping("/tienda/{tiendaId}")
    public List<Tornillo> obtenerPorTienda(@PathVariable Integer tiendaId) {
        return tornilloService.obtenerPorTienda(tiendaId);
    }

    @GetMapping("/tienda/{tiendaId}/familia/{familia}")
    public List<Tornillo> obtenerPorTiendaYFamilia(
            @PathVariable Integer tiendaId,
            @PathVariable String familia
    ) {
        return tornilloService.obtenerPorTiendaYFamilia(tiendaId, familia);
    }

    @GetMapping("/tienda/{tiendaId}/familia/{familia}/modulo/{nombreModulo}")
    public List<Tornillo> obtenerPorTiendaFamiliaYModulo(
            @PathVariable int tiendaId,
            @PathVariable String familia,
            @PathVariable String nombreModulo
    ) {
        return tornilloService.obtenerPorTiendaFamiliaYModulo(tiendaId, familia, nombreModulo);
    }

    @GetMapping("/tienda/{tiendaId}/familia/{familia}/modulos")
    public List<String> obtenerModulos(
            @PathVariable int tiendaId,
            @PathVariable String familia
    ) {
        return tornilloService.obtenerModulosPorTiendaYFamilia(tiendaId, familia);
    }

    @GetMapping("/dto/tienda/{tiendaId}/familia/{familia}/modulo/{nombreModulo}")
    public List<TornilloConProductoDTO> obtenerDTO(
            @PathVariable int tiendaId,
            @PathVariable String familia,
            @PathVariable String nombreModulo
    ) {
        return tornilloService.obtenerDTOPorTiendaFamiliaYModulo(tiendaId, familia, nombreModulo);
    }

    // Un tornillo (único) por tienda + producto, con datos de producto en el DTO ===
    @GetMapping("/dto/tienda/{tiendaId}/producto/{productoCodigo}")
    public TornilloConProductoDTO obtenerDTOPorTiendaYProducto(
            @PathVariable Integer tiendaId,
            @PathVariable Integer productoCodigo
    ) {
        return tornilloService
                .obtenerDTOPorTiendaYProducto(tiendaId, productoCodigo)
                .orElseThrow(() ->
                        new RuntimeException("No existe tornillo para tienda " + tiendaId + " y producto " + productoCodigo));
    }


}
