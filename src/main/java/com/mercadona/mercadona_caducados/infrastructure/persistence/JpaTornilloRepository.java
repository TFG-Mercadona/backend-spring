package com.mercadona.mercadona_caducados.infrastructure.persistence;

import com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO;
import com.mercadona.mercadona_caducados.domain.model.Tornillo;
import com.mercadona.mercadona_caducados.domain.repository.TornilloRepository;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaTornilloRepository implements TornilloRepository {

    private final SpringDataTornilloRepository springRepo;
    private final SpringDataProductoRepository productoRepo;

    @Autowired
    public JpaTornilloRepository(SpringDataTornilloRepository springRepo, SpringDataProductoRepository productoRepo) {
        this.springRepo = springRepo;
        this.productoRepo = productoRepo;
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
    
    @Override
    public Optional<Tornillo> obtenerPorTiendaYProducto(Integer tiendaId, Integer productoCodigo) {
        return springRepo.findByTiendaIdAndProductoCodigo(tiendaId, productoCodigo)
                .map(TornilloEntity::toDomain);
    }

    @Override
    public Optional<TornilloConProductoDTO> obtenerDTOPorTiendaYProducto(Integer tiendaId, Integer productoCodigo) {
        return springRepo.findByTiendaIdAndProductoCodigo(tiendaId, productoCodigo).map(te -> {
            // Si 'codigo' es PK de ProductoEntity, findById(productoCodigo) funciona.
            // Si no lo fuera, añade findByCodigo(...) en SpringDataProductoRepository y úsalo aquí.
            var prod = productoRepo.findById(productoCodigo).orElse(null);

            TornilloConProductoDTO dto = new TornilloConProductoDTO(
                    te.getId(),
                    te.getProductoCodigo(),
                    te.getTiendaId(),
                    toYmd(te.getFechaCaducidad()),
                    toYmd(te.getFechaRetirada()),
                    te.getNombreModulo(),
                    te.getFila(),
                    te.getColumna(),
                    prod != null ? prod.getNombre() : null,
                    prod != null ? prod.getImagenUrl() : null
            );

            // Metemos caducidad y familia sin estropear lo anterior
            if (prod != null) {
                dto.familia = prod.getFamilia();
                dto.caducidadDias = prod.getCaducidadDias();
            }
            
            return dto;
        });
    }

    @Override
    public void updateFechaCaducidad(Long id, LocalDate nuevaFecha) {
        TornilloEntity te = springRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tornillo no encontrado: " + id));
        te.setFechaCaducidad(nuevaFecha);
        // El trigger en BD recalculará fecha_retirada tras el UPDATE
        springRepo.save(te);
    }   

    private static String toYmd(LocalDate d) {
        return d != null ? d.toString() : null;
    }

}
