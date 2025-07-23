package com.mercadona.mercadona_caducados.infrastructure.persistence;

import com.mercadona.mercadona_caducados.infrastructure.persistence.JpaTiendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTiendaRepository extends JpaRepository<JpaTiendaEntity, Integer> {}
