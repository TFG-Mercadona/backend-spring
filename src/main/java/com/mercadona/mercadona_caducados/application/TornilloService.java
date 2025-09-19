package com.mercadona.mercadona_caducados.application;

import com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO;
import com.mercadona.mercadona_caducados.domain.model.Tornillo;
import com.mercadona.mercadona_caducados.domain.repository.TornilloRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.*;
import java.time.LocalDate;

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

    public List<TornilloConProductoDTO> obtenerDTOPorTiendaFamiliaYModulo(int tiendaId, String familia, String nombreModulo) {
        return tornilloRepository.findDTOByTiendaIdAndFamiliaAndNombreModulo(tiendaId, familia, nombreModulo);
    }

    // Obtener un único DTO por tienda + producto
    public Optional<TornilloConProductoDTO> obtenerDTOPorTiendaYProducto(Integer tiendaId, Integer productoCodigo) {
         return tornilloRepository.obtenerDTOPorTiendaYProducto(tiendaId, productoCodigo);
    }

    // NUEVO: actualizar fecha de caducidad (formato "YYYY-MM-DD")
    @Transactional
    public void actualizarFechaCaducidad(Long id, String fechaYmd) {
        if (fechaYmd == null || !fechaYmd.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use YYYY-MM-DD.");
        }
        LocalDate nueva = LocalDate.parse(fechaYmd);
        // Verifica existencia para devolver mensaje limpio si no existe
        tornilloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tornillo no encontrado: " + id));
        tornilloRepository.updateFechaCaducidad(id, nueva);
    }

public List<TornilloConProductoDTO> obtenerCaducadosDTO(int tiendaId, String familia, String nombreModulo) {
    List<TornilloConProductoDTO> base =
        (nombreModulo == null || nombreModulo.isBlank())
            ? tornilloRepository.findDTOCaducadosByTiendaIdAndFamilia(tiendaId, familia)
            : tornilloRepository.findDTOCaducadosByTiendaIdAndFamiliaAndNombreModulo(tiendaId, familia, nombreModulo);

    return ordenarZigZagPorModulo(base);
}

private List<TornilloConProductoDTO> ordenarZigZagPorModulo(List<TornilloConProductoDTO> items) {
    // group by modulo
    Map<String, List<TornilloConProductoDTO>> byModulo = items.stream()
            .collect(Collectors.groupingBy(dto -> dto.nombreModulo));

    // orden por módulo numérico (usa tu extraerNumeroModulo ya existente)
    return byModulo.entrySet().stream()
            .sorted(Comparator.comparingInt(e -> extraerNumeroModulo(e.getKey())))
            .flatMap(e -> e.getValue().stream()
                .sorted((a, b) -> {
                    int cf = Integer.compare(a.fila, b.fila); // fila asc
                    if (cf != 0) return cf;
                    boolean odd = (a.fila % 2) != 0;          // impar: izq→der; par: der→izq
                    return odd ? Integer.compare(a.columna, b.columna)
                               : Integer.compare(b.columna, a.columna);
                })
            )
            .collect(Collectors.toList());
}






}
