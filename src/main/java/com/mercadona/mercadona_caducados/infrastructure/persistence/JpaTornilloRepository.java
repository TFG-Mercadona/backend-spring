package com.mercadona.mercadona_caducados.infrastructure.persistence;

import com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO;
import com.mercadona.mercadona_caducados.domain.model.Tornillo;
import com.mercadona.mercadona_caducados.domain.repository.TornilloRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaTornilloRepository implements TornilloRepository {

    private final SpringDataTornilloRepository springRepo;

    public JpaTornilloRepository(SpringDataTornilloRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public List<Tornillo> findAll() {
        return springRepo.findAll().stream()
                .map(TornilloEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tornillo> findByTiendaId(Integer tiendaId) {
        return springRepo.findByTiendaId(tiendaId).stream()
                .map(TornilloEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tornillo> findByTiendaIdAndFamilia(Integer tiendaId, String familia) {
        return springRepo.findByTiendaIdAndFamilia(tiendaId, familia).stream()
                .map(TornilloEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Tornillo> findById(Long id) {
        return springRepo.findById(id).map(TornilloEntity::toDomain);
    }

    @Override
    public List<Tornillo> findByTiendaIdAndFamiliaAndNombreModulo(int tiendaId, String familia, String nombreModulo) {
        return springRepo.findByTiendaIdAndFamiliaAndNombreModulo(tiendaId, familia, nombreModulo)
                .stream()
                .map(TornilloEntity::toDomain)
                .toList();
    }

    @Override
    public List<String> obtenerModulosPorTiendaYFamilia(int tiendaId, String familia) {
        return springRepo.findDistinctNombreModuloByTiendaIdAndFamilia(tiendaId, familia);
    }

    @Override
    public List<TornilloConProductoDTO> findDTOByTiendaIdAndFamiliaAndNombreModulo(int tiendaId, String familia, String nombreModulo) {
        return springRepo.findDTOByTiendaIdAndFamiliaAndNombreModulo(tiendaId, familia, nombreModulo);
    }


}
