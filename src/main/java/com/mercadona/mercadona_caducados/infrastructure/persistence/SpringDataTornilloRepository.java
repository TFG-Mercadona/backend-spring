package com.mercadona.mercadona_caducados.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataTornilloRepository extends JpaRepository<TornilloEntity, Long> {

    List<TornilloEntity> findByTiendaId(Integer tiendaId);

    @Query("""
        SELECT t FROM TornilloEntity t
        JOIN ProductoEntity p ON t.productoCodigo = p.codigo
        WHERE t.tiendaId = :tiendaId AND p.familia = :familia
    """)
    List<TornilloEntity> findByTiendaIdAndFamilia(
        @Param("tiendaId") Integer tiendaId,
        @Param("familia") String familia
    );

    @Query("""
        SELECT t FROM TornilloEntity t
        JOIN ProductoEntity p ON t.productoCodigo = p.codigo
        WHERE t.tiendaId = :tiendaId AND p.familia = :familia AND t.nombreModulo = :nombreModulo
    """)
    List<TornilloEntity> findByTiendaIdAndFamiliaAndNombreModulo(
        @Param("tiendaId") Integer tiendaId,
        @Param("familia") String familia,
        @Param("nombreModulo") String nombreModulo
    );

    @Query("""
        SELECT DISTINCT t.nombreModulo FROM TornilloEntity t
        JOIN ProductoEntity p ON t.productoCodigo = p.codigo
        WHERE t.tiendaId = :tiendaId AND p.familia = :familia
    """)
    List<String> findDistinctNombreModuloByTiendaIdAndFamilia(
        @Param("tiendaId") Integer tiendaId,
        @Param("familia") String familia
    );
}
