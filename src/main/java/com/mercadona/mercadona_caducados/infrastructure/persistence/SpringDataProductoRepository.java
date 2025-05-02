package com.mercadona.mercadona_caducados.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataProductoRepository extends JpaRepository<ProductoEntity, Integer> {
    List<ProductoEntity> findByFamilia(String familia);
}