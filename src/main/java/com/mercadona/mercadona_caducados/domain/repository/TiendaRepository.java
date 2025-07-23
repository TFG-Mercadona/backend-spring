package com.mercadona.mercadona_caducados.domain.repository;

import com.mercadona.mercadona_caducados.domain.model.Tienda;
import java.util.Optional;

public interface TiendaRepository {
    Optional<Tienda> findById(Integer id);
}
