package com.mercadona.mercadona_caducados.domain.repository;

import com.mercadona.mercadona_caducados.domain.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {

    List<Producto> findAll();

    Optional<Producto> findByCodigo(int codigo);

    List<Producto> findByFamilia(String familia);
}
