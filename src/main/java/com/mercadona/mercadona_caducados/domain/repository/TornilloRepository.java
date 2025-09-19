package com.mercadona.mercadona_caducados.domain.repository;

import com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO;
import com.mercadona.mercadona_caducados.domain.model.Tornillo;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface TornilloRepository {
    List<Tornillo> findAll();
    List<Tornillo> findByTiendaId(Integer tiendaId);
    List<Tornillo> findByTiendaIdAndFamilia(Integer tiendaId, String familia);
    List<Tornillo> findByTiendaIdAndFamiliaAndNombreModulo(int tiendaId, String familia, String nombreModulo);
    List<String> obtenerModulosPorTiendaYFamilia(int tiendaId, String familia);
    Optional<Tornillo> findById(Long id);
    List<TornilloConProductoDTO> findDTOByTiendaIdAndFamiliaAndNombreModulo(int tiendaId, String familia, String nombreModulo);
    Optional<TornilloConProductoDTO> obtenerDTOPorTiendaYProducto(Integer tiendaId, Integer productoCodigo);
    Optional<Tornillo> obtenerPorTiendaYProducto(Integer tiendaId, Integer productoCodigo);
    void updateFechaCaducidad(Long id, LocalDate nuevaFecha);
    List<TornilloConProductoDTO> findDTOCaducadosByTiendaIdAndFamilia(int tiendaId, String familia);
List<TornilloConProductoDTO> findDTOCaducadosByTiendaIdAndFamiliaAndNombreModulo(int tiendaId, String familia, String nombreModulo);
}