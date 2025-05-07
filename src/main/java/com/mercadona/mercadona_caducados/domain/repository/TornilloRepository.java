package com.mercadona.mercadona_caducados.domain.repository;

import com.mercadona.mercadona_caducados.domain.model.Tornillo;
import com.mercadona.mercadona_caducados.domain.dto.TornilloConProductoDTO;

import java.util.List;
import java.util.Optional;

public interface TornilloRepository {
    List<Tornillo> findAll();
    List<Tornillo> findByTiendaId(Integer tiendaId);
    List<Tornillo> findByTiendaIdAndFamilia(Integer tiendaId, String familia);
    List<Tornillo> findByTiendaIdAndFamiliaAndNombreModulo(int tiendaId, String familia, String nombreModulo);
    List<String> obtenerModulosPorTiendaYFamilia(int tiendaId, String familia);
    Optional<Tornillo> findById(Long id);
    List<TornilloConProductoDTO> findDTOByTiendaIdAndFamiliaAndNombreModulo(int tiendaId, String familia, String nombreModulo);
}
