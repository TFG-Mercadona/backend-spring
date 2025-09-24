package com.mercadona.mercadona_caducados.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpringDataCambioCaducidadRepository
        extends JpaRepository<TornilloCambioCaducidadEntity, Long> {

    // ya podías tener estos dos:
    List<TornilloCambioCaducidadEntity> findByTiendaIdAndFechaCambioBetween(
            Integer tiendaId, LocalDateTime desde, LocalDateTime hasta);

    List<TornilloCambioCaducidadEntity> findByTiendaIdAndFamiliaAndFechaCambioBetween(
            Integer tiendaId, String familia, LocalDateTime desde, LocalDateTime hasta);

    // NUEVO: con familia opcional (null o "", no filtra)
    @Query("""
           SELECT c
           FROM TornilloCambioCaducidadEntity c
           WHERE c.tiendaId = :tiendaId
             AND c.fechaCambio BETWEEN :desde AND :hasta
             AND (:familia IS NULL OR :familia = '' OR c.familia = :familia)
           ORDER BY c.fechaCambio DESC
           """)
    List<TornilloCambioCaducidadEntity> findCambiosDeRango(
            @Param("tiendaId") Integer tiendaId,
            @Param("familia") String familia,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    @Modifying
    @Query("UPDATE TornilloCambioCaducidadEntity c SET c.contrastado = :valor WHERE c.id = :id")
    int marcarContrastado(@Param("id") Long id, @Param("valor") boolean valor);   
}