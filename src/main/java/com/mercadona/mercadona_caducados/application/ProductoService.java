package com.mercadona.mercadona_caducados.application;

import com.mercadona.mercadona_caducados.domain.model.Producto;
import com.mercadona.mercadona_caducados.domain.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorCodigo(int codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    public List<Producto> obtenerPorFamilia(String familia) {
        return productoRepository.findByFamilia(familia);
    }
}
