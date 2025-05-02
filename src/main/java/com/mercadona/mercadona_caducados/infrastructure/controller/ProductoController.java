package com.mercadona.mercadona_caducados.infrastructure.controller;

import com.mercadona.mercadona_caducados.application.ProductoService;
import com.mercadona.mercadona_caducados.domain.model.Producto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> getAll() {
        return productoService.obtenerTodos();
    }

    @GetMapping("/{codigo}")
    public Optional<Producto> getByCodigo(@PathVariable int codigo) {
        return productoService.obtenerPorCodigo(codigo);
    }

    @GetMapping("/familia/{familia}")
    public List<Producto> getByFamilia(@PathVariable String familia) {
        return productoService.obtenerPorFamilia(familia);
    }
}
