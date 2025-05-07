package com.mercadona.mercadona_caducados.application;

import com.mercadona.mercadona_caducados.domain.model.Tornillo;
import com.mercadona.mercadona_caducados.domain.repository.TornilloRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;

@Service
public class TornilloService {

    private final TornilloRepository tornilloRepository;

    public TornilloService(TornilloRepository tornilloRepository) {
        this.tornilloRepository = tornilloRepository;
    }

    public List<Tornillo> obtenerTodos() {
        return tornilloRepository.findAll();
    }

    public Optional<Tornillo> obtenerPorId(Long id) {
        return tornilloRepository.findById(id);
    }

    public List<Tornillo> obtenerPorTienda(Integer tiendaId) {
        return tornilloRepository.findByTiendaId(tiendaId);
    }

    public List<Tornillo> obtenerPorTiendaYFamilia(Integer tiendaId, String familia) {
        return tornilloRepository.findByTiendaIdAndFamilia(tiendaId, familia);
    }

    public List<Tornillo> obtenerPorTiendaFamiliaYModulo(int tiendaId, String familia, String nombreModulo) {
        return tornilloRepository.findByTiendaIdAndFamiliaAndNombreModulo(tiendaId, familia, nombreModulo);
    }

    public List<String> obtenerModulosPorTiendaYFamilia(int tiendaId, String familia) {
        return tornilloRepository.obtenerModulosPorTiendaYFamilia(tiendaId, familia).stream()
            .sorted(Comparator.comparingInt(this::extraerNumeroModulo))
            .toList();
    }

    private int extraerNumeroModulo(String nombreModulo) {
        // Extrae el número del tipo "Módulo X"
        try {
            return Integer.parseInt(nombreModulo.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE; // Los que no tengan número van al final
        }
    }

}
