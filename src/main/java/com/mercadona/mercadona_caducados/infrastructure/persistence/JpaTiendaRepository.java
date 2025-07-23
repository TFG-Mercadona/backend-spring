package com.mercadona.mercadona_caducados.infrastructure.persistence;

import com.mercadona.mercadona_caducados.domain.model.Tienda;
import com.mercadona.mercadona_caducados.domain.repository.TiendaRepository;
import com.mercadona.mercadona_caducados.infrastructure.persistence.JpaTiendaEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaTiendaRepository implements TiendaRepository {

    private final SpringDataTiendaRepository repository;

    public JpaTiendaRepository(SpringDataTiendaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Tienda> findById(Integer id) {
        return repository.findById(id).map(
            entity -> new Tienda(
                entity.getId(),
                entity.getNombre(),
                entity.getUbicacion(),
                entity.getPasswordHashed()
            )
        );
    }
}
