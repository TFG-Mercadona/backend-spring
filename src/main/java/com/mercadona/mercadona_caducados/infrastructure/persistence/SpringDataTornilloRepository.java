package com.mercadona.mercadona_caducados.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO;

import java.util.List;
import java.util.Optional;

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

    // ===== DTOs con join a Producto, incluyendo familia y caducidadDias =====

    @Query("""
        SELECT new com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO(
            t.id, t.productoCodigo, t.tiendaId,
            CAST(t.fechaCaducidad AS string), CAST(t.fechaRetirada AS string),
            t.nombreModulo, t.fila, t.columna,
            p.nombre, p.imagenUrl,
            p.familia, p.caducidadDias
        )
        FROM TornilloEntity t
        JOIN ProductoEntity p ON t.productoCodigo = p.codigo
        WHERE t.tiendaId = :tiendaId AND p.familia = :familia AND t.nombreModulo = :nombreModulo
    """)
    List<TornilloConProductoDTO> findDTOByTiendaIdAndFamiliaAndNombreModulo(
        @Param("tiendaId") Integer tiendaId,
        @Param("familia") String familia,
        @Param("nombreModulo") String nombreModulo
    );

    // 1) Entidad (por si la necesitas en otras capas)
    Optional<TornilloEntity> findByTiendaIdAndProductoCodigo(Integer tiendaId, Integer productoCodigo);

    // 2) DTO (join con producto) por tienda + producto
    @Query("""
        SELECT new com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO(
            t.id, t.productoCodigo, t.tiendaId,
            CAST(t.fechaCaducidad AS string), CAST(t.fechaRetirada AS string),
            t.nombreModulo, t.fila, t.columna,
            p.nombre, p.imagenUrl,
            p.familia, p.caducidadDias
        )
        FROM TornilloEntity t
        JOIN ProductoEntity p ON t.productoCodigo = p.codigo
        WHERE t.tiendaId = :tiendaId AND t.productoCodigo = :productoCodigo
    """)
    Optional<TornilloConProductoDTO> findDTOByTiendaIdAndProductoCodigo(
        @Param("tiendaId") Integer tiendaId,
        @Param("productoCodigo") Integer productoCodigo
    );

    // 3) Caducados por tienda + familia
    @Query("""
      SELECT new com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO(
          t.id, t.productoCodigo, t.tiendaId,
          CAST(t.fechaCaducidad AS string), CAST(t.fechaRetirada AS string),
          t.nombreModulo, t.fila, t.columna,
          p.nombre, p.imagenUrl,
          p.familia, p.caducidadDias
      )
      FROM TornilloEntity t
      JOIN ProductoEntity p ON t.productoCodigo = p.codigo
      WHERE t.tiendaId = :tiendaId
        AND p.familia = :familia
        AND t.fechaRetirada < CURRENT_DATE
      ORDER BY t.fechaRetirada ASC, t.nombreModulo ASC, t.fila ASC, t.columna ASC
    """)
    List<TornilloConProductoDTO> findDTOCaducadosByTiendaIdAndFamilia(
        @Param("tiendaId") int tiendaId,
        @Param("familia") String familia
    );

    // 4) Caducados por tienda + familia + módulo
    @Query("""
      SELECT new com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO(
          t.id, t.productoCodigo, t.tiendaId,
          CAST(t.fechaCaducidad AS string), CAST(t.fechaRetirada AS string),
          t.nombreModulo, t.fila, t.columna,
          p.nombre, p.imagenUrl,
          p.familia, p.caducidadDias
      )
      FROM TornilloEntity t
      JOIN ProductoEntity p ON t.productoCodigo = p.codigo
      WHERE t.tiendaId = :tiendaId
        AND p.familia = :familia
        AND t.nombreModulo = :nombreModulo
        AND t.fechaRetirada < CURRENT_DATE
      ORDER BY t.fechaRetirada ASC, t.fila ASC, t.columna ASC
    """)
    List<TornilloConProductoDTO> findDTOCaducadosByTiendaIdAndFamiliaAndNombreModulo(
        @Param("tiendaId") int tiendaId,
        @Param("familia") String familia,
        @Param("nombreModulo") String nombreModulo
    );
}
