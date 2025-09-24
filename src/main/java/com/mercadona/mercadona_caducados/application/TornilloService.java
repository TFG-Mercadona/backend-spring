package com.mercadona.mercadona_caducados.application;

import com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO;
import com.mercadona.mercadona_caducados.application.dto.UpdateTornilloBasicoRequest;
import com.mercadona.mercadona_caducados.application.dto.CreateTornilloRequest;
import com.mercadona.mercadona_caducados.domain.model.Tornillo;
import com.mercadona.mercadona_caducados.domain.repository.TornilloRepository;
import com.mercadona.mercadona_caducados.infrastructure.persistence.SpringDataTornilloRepository;
import com.mercadona.mercadona_caducados.infrastructure.persistence.SpringDataCambioCaducidadRepository;
import com.mercadona.mercadona_caducados.infrastructure.persistence.TornilloCambioCaducidadEntity;
import com.mercadona.mercadona_caducados.infrastructure.persistence.TornilloEntity;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TornilloService {

    private final TornilloRepository tornilloRepository;            // dominio
    private final SpringDataTornilloRepository springTornillos;     // JPA nativo
    private final SpringDataCambioCaducidadRepository cambiosRepo;  // auditoría

    public TornilloService(TornilloRepository tornilloRepository,
                           SpringDataTornilloRepository springTornillos,
                           SpringDataCambioCaducidadRepository cambiosRepo) {
        this.tornilloRepository = tornilloRepository;
        this.springTornillos = springTornillos;
        this.cambiosRepo = cambiosRepo;
    }

    /* ==========================
     * Lecturas existentes
     * ========================== */

    public List<Tornillo> obtenerTodos() { return tornilloRepository.findAll(); }

    public Optional<Tornillo> obtenerPorId(Long id) { return tornilloRepository.findById(id); }

    public List<Tornillo> obtenerPorTienda(Integer tiendaId) { return tornilloRepository.findByTiendaId(tiendaId); }

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
        try { return Integer.parseInt(nombreModulo.replaceAll("\\D+", "")); }
        catch (NumberFormatException e) { return Integer.MAX_VALUE; }
    }

    public List<TornilloConProductoDTO> obtenerDTOPorTiendaFamiliaYModulo(int tiendaId, String familia, String nombreModulo) {
        return tornilloRepository.findDTOByTiendaIdAndFamiliaAndNombreModulo(tiendaId, familia, nombreModulo);
    }

    public Optional<TornilloConProductoDTO> obtenerDTOPorTiendaYProducto(Integer tiendaId, Integer productoCodigo) {
        return tornilloRepository.obtenerDTOPorTiendaYProducto(tiendaId, productoCodigo);
    }

    /* ==========================
     * Actualizaciones existentes
     * ========================== */

    // Actualizar fecha de caducidad (YYYY-MM-DD) + log de auditoría
    @Transactional
    public void actualizarFechaCaducidad(Long id, String fechaYmd) {
        if (fechaYmd == null || !fechaYmd.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use YYYY-MM-DD.");
        }
        LocalDate nueva = LocalDate.parse(fechaYmd);

        TornilloEntity e = springTornillos.findById(id)
                .orElseThrow(() -> new RuntimeException("Tornillo no encontrado: " + id));

        LocalDate anterior = e.getFechaCaducidad();
        e.setFechaCaducidad(nueva);
        springTornillos.save(e);

        logCambioCaducidad(e, anterior, nueva, null);
    }

    /* ==========================
     * Caducados (orden zig-zag)
     * ========================== */

    public List<TornilloConProductoDTO> obtenerCaducadosDTO(int tiendaId, String familia, String nombreModulo) {
        List<TornilloConProductoDTO> base =
                (nombreModulo == null || nombreModulo.isBlank())
                        ? tornilloRepository.findDTOCaducadosByTiendaIdAndFamilia(tiendaId, familia)
                        : tornilloRepository.findDTOCaducadosByTiendaIdAndFamiliaAndNombreModulo(tiendaId, familia, nombreModulo);

        return ordenarZigZagPorModulo(base);
    }

    private List<TornilloConProductoDTO> ordenarZigZagPorModulo(List<TornilloConProductoDTO> items) {
        Map<String, List<TornilloConProductoDTO>> byModulo = items.stream()
                .collect(Collectors.groupingBy(dto -> dto.nombreModulo));

        return byModulo.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> extraerNumeroModulo(e.getKey())))
                .flatMap(e -> e.getValue().stream()
                        .sorted((a, b) -> {
                            int cf = Integer.compare(a.fila, b.fila);
                            if (cf != 0) return cf;
                            boolean odd = (a.fila % 2) != 0;
                            return odd ? Integer.compare(a.columna, b.columna)
                                       : Integer.compare(b.columna, a.columna);
                        })
                )
                .collect(Collectors.toList());
    }

    /* ==========================
     * Editar tornillo (básico)
     * ========================== */

    @Transactional
    public TornilloConProductoDTO editarTornilloBasico(Long id, UpdateTornilloBasicoRequest req) {
        TornilloEntity e = springTornillos.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tornillo no encontrado: " + id));

        // Posición
        if (req.getNombreModulo() != null && !req.getNombreModulo().isBlank()) e.setNombreModulo(req.getNombreModulo());
        if (req.getFila() != null)     e.setFila(req.getFila());
        if (req.getColumna() != null)  e.setColumna(req.getColumna());

        // Fecha de caducidad (con auditoría)
        LocalDate anterior = e.getFechaCaducidad();
        boolean cambiaFecha = (req.getFechaCaducidad() != null && !req.getFechaCaducidad().isBlank());
        if (cambiaFecha) {
            if (!req.getFechaCaducidad().matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new IllegalArgumentException("Formato de fecha inválido. Use YYYY-MM-DD.");
            }
            LocalDate nuevaCad = LocalDate.parse(req.getFechaCaducidad());
            e.setFechaCaducidad(nuevaCad);

            if (req.getCaducidadDias() != null) {
                e.setFechaRetirada(nuevaCad.minusDays(req.getCaducidadDias()));
            }
        }

        springTornillos.save(e);

        if (cambiaFecha) {
            logCambioCaducidad(e, anterior, e.getFechaCaducidad(), req.getCaducidadDias());
        }

        return springTornillos
                .findDTOByTiendaIdAndProductoCodigo(e.getTiendaId(), e.getProductoCodigo())
                .orElseThrow(() -> new IllegalStateException("No se pudo recuperar DTO tras editar tornillo"));
    }

    /* ==========================
     * Eliminar
     * ========================== */

    @Transactional
    public void eliminarPorId(Long id) {
        if (!springTornillos.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tornillo no encontrado: " + id);
        }
        springTornillos.deleteById(id);
    }

    /* ==========================
     * Crear (básico)
     * ========================== */

    @Transactional
    public TornilloConProductoDTO crearTornilloBasico(CreateTornilloRequest req) {
        // validación mínima
        if (req.getTiendaId() == null ||
            req.getProductoCodigo() == null ||
            req.getNombreModulo() == null || req.getNombreModulo().isBlank() ||
            req.getFila() == null ||
            req.getColumna() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan campos obligatorios");
        }

        // fecha
        LocalDate cad = null;
        if (req.getFechaCaducidad() != null && !req.getFechaCaducidad().isBlank()) {
            if (!req.getFechaCaducidad().matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha inválido (YYYY-MM-DD)");
            }
            cad = LocalDate.parse(req.getFechaCaducidad());
        }

        // retirada si hay caducidadDias
        LocalDate retirada = null;
        if (cad != null && req.getCaducidadDias() != null) {
            if (req.getCaducidadDias() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "caducidadDias no puede ser negativo");
            }
            retirada = cad.minusDays(req.getCaducidadDias());
        }

        // persistencia
        TornilloEntity e = new TornilloEntity();
        e.setTiendaId(req.getTiendaId());
        e.setProductoCodigo(req.getProductoCodigo());
        e.setNombreModulo(req.getNombreModulo());
        e.setFila(req.getFila());
        e.setColumna(req.getColumna());
        e.setFechaCaducidad(cad);
        e.setFechaRetirada(retirada);

        springTornillos.save(e);

        // DTO rico
        return springTornillos
                .findDTOByTiendaIdAndProductoCodigo(e.getTiendaId(), e.getProductoCodigo())
                .orElseGet(() -> new TornilloConProductoDTO(
                        e.getId(),
                        e.getProductoCodigo(),
                        e.getTiendaId(),
                        e.getFechaCaducidad() != null ? e.getFechaCaducidad().toString() : null,
                        e.getFechaRetirada()  != null ? e.getFechaRetirada().toString()  : null,
                        e.getNombreModulo(),
                        e.getFila(),
                        e.getColumna(),
                        null,
                        null
                ));
    }

    /* ==========================
     * Organizar: mover/swap
     * ========================== */

    @Transactional
    public TornilloConProductoDTO moverPosicion(Long id, String nombreModulo, Integer fila, Integer columna) {
        if (nombreModulo == null || nombreModulo.isBlank() || fila == null || columna == null) {
            throw new IllegalArgumentException("nombreModulo, fila y columna son obligatorios");
        }

        TornilloEntity a = springTornillos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tornillo no encontrado: " + id));

        List<TornilloEntity> ocupantes = springTornillos
                .findAllByTiendaIdAndNombreModuloAndFilaAndColumna(a.getTiendaId(), nombreModulo, fila, columna);

        boolean yaEstaEnSitio = ocupantes.stream().anyMatch(e ->
                e.getId().equals(a.getId())
                        && Objects.equals(e.getNombreModulo(), nombreModulo)
                        && Objects.equals(e.getFila(), fila)
                        && Objects.equals(e.getColumna(), columna)
        );
        if (yaEstaEnSitio) {
            return springTornillos.findDTOByTiendaIdAndProductoCodigo(a.getTiendaId(), a.getProductoCodigo())
                    .orElseThrow(() -> new IllegalStateException("No se pudo recuperar DTO"));
        }

        TornilloEntity b = ocupantes.stream()
                .filter(e -> !e.getId().equals(a.getId()))
                .findFirst()
                .orElse(null);

        long distintosDeA = ocupantes.stream().filter(e -> !e.getId().equals(a.getId())).count();
        if (distintosDeA > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La celda destino está ocupada por múltiples tornillos. Limpia duplicados antes de organizar.");
        }

        if (b != null) {
            String oldModulo = a.getNombreModulo();
            Integer oldFila = a.getFila();
            Integer oldCol = a.getColumna();

            a.setNombreModulo(nombreModulo);
            a.setFila(fila);
            a.setColumna(columna);

            b.setNombreModulo(oldModulo);
            b.setFila(oldFila);
            b.setColumna(oldCol);

            springTornillos.save(b);
            springTornillos.save(a);
        } else {
            a.setNombreModulo(nombreModulo);
            a.setFila(fila);
            a.setColumna(columna);
            springTornillos.save(a);
        }

        return springTornillos.findDTOByTiendaIdAndProductoCodigo(a.getTiendaId(), a.getProductoCodigo())
                .orElseThrow(() -> new IllegalStateException("No se pudo recuperar DTO tras mover tornillo"));
    }

    /* ==========================
     * Auditoría: helper
     * ========================== */

    private void logCambioCaducidad(TornilloEntity e, LocalDate fechaAnterior, LocalDate fechaNueva, Integer caducidadDias) {
        TornilloCambioCaducidadEntity c = new TornilloCambioCaducidadEntity();
        c.setTornilloId(e.getId());
        c.setTiendaId(e.getTiendaId());
        c.setProductoCodigo(e.getProductoCodigo());
        // intentar enriquecer familia desde la proyección (TornilloConProductoDTO)
        try {
            Optional<TornilloConProductoDTO> dtoOpt =
                    springTornillos.findDTOByTiendaIdAndProductoCodigo(e.getTiendaId(), e.getProductoCodigo());
            dtoOpt.ifPresent(dto -> c.setFamilia(dto.familia));
        } catch (Exception ignore) { /* si falla, lo dejamos en null */ }

        c.setNombreModulo(e.getNombreModulo());
        c.setFila(e.getFila());
        c.setColumna(e.getColumna());
        c.setFechaAnterior(fechaAnterior);
        c.setFechaNueva(fechaNueva);
        c.setCaducidadDias(caducidadDias);
        c.setFechaCambio(java.time.LocalDateTime.now());
        c.setContrastado(false);
        cambiosRepo.save(c);
    }
}
