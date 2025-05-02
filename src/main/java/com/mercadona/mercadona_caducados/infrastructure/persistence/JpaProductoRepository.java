package com.mercadona.mercadona_caducados.infrastructure.persistence;

import com.mercadona.mercadona_caducados.domain.model.Producto;
import com.mercadona.mercadona_caducados.domain.repository.ProductoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaProductoRepository implements ProductoRepository {

    private final SpringDataProductoRepository jpa;

    public JpaProductoRepository(SpringDataProductoRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Producto> findAll() {
        return jpa.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Producto> findByCodigo(int codigo) {
        return jpa.findById(codigo).map(this::toDomain);
    }

    @Override
    public List<Producto> findByFamilia(String familia) {
        return jpa.findByFamilia(familia).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Producto toDomain(ProductoEntity entity) {
        return new Producto(
                entity.getCodigo(),
                entity.getNombre(),
                entity.getFamilia(),
                entity.getCaducidadDias(),
                entity.getImagenUrl()
        );
    }
}
