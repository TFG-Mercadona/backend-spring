package com.mercadona.mercadona_caducados.infrastructure.controller;

import com.mercadona.mercadona_caducados.application.TornilloService;
import com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO;
import com.mercadona.mercadona_caducados.application.dto.UpdateTornilloBasicoRequest;
import com.mercadona.mercadona_caducados.application.dto.CreateTornilloRequest;
import com.mercadona.mercadona_caducados.domain.model.Tornillo;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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

    // === NUEVO: actualizar fecha de caducidad por id ===
    @PutMapping("/{id}/fecha-caducidad")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void actualizarFechaCaducidad(
            @PathVariable Long id,
            @RequestBody FechaCaducidadRequest req
    ) {
        if (req == null || req.getFechaCaducidad() == null) {
            throw new IllegalArgumentException("Body requerido: { \"fechaCaducidad\": \"YYYY-MM-DD\" }");
        }
        tornilloService.actualizarFechaCaducidad(id, req.getFechaCaducidad());
    }

    // DTO request simple
    public static class FechaCaducidadRequest {
        private String fechaCaducidad;
        public String getFechaCaducidad() { return fechaCaducidad; }
        public void setFechaCaducidad(String fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }
    }

    @GetMapping("/dto/tienda/{tiendaId}/familia/{familia}/caducados")
public List<TornilloConProductoDTO> obtenerCaducados(
        @PathVariable int tiendaId,
        @PathVariable String familia,
        @RequestParam(value = "modulo", required = false) String nombreModulo
) {
    return tornilloService.obtenerCaducadosDTO(tiendaId, familia, nombreModulo);
}

// TornilloController.java (añadir)
@PutMapping("/editar/{id}")
public ResponseEntity<TornilloConProductoDTO> editarTornilloBasico(
        @PathVariable Long id,
        @RequestBody UpdateTornilloBasicoRequest req) {
    TornilloConProductoDTO dto = tornilloService.editarTornilloBasico(id, req);
    return ResponseEntity.ok(dto);
}

@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void eliminarTornillo(@PathVariable Long id) {
    tornilloService.eliminarPorId(id);
}

@PostMapping
public ResponseEntity<TornilloConProductoDTO> crearTornillo(@RequestBody CreateTornilloRequest req) {
    TornilloConProductoDTO dto = tornilloService.crearTornilloBasico(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
}



}
