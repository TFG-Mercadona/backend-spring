package com.mercadona.mercadona_caducados.application;

import com.mercadona.mercadona_caducados.application.dto.TornilloConProductoDTO;
import com.mercadona.mercadona_caducados.application.dto.UpdateTornilloBasicoRequest;
import com.mercadona.mercadona_caducados.application.dto.CreateTornilloRequest;
import com.mercadona.mercadona_caducados.domain.model.Tornillo;
import com.mercadona.mercadona_caducados.domain.repository.TornilloRepository;
import com.mercadona.mercadona_caducados.infrastructure.persistence.SpringDataTornilloRepository;
import com.mercadona.mercadona_caducados.infrastructure.persistence.TornilloEntity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TornilloService {

    private final TornilloRepository tornilloRepository;            // dominio (ya existía)
    private final SpringDataTornilloRepository springTornillos;     // JPA nativo (para editar/guardar rápido)

    public TornilloService(TornilloRepository tornilloRepository,
                           SpringDataTornilloRepository springTornillos) {
        this.tornilloRepository = tornilloRepository;
        this.springTornillos = springTornillos;
    }

    /* ==========================
     * Lecturas existentes
     * ========================== */

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
        try {
            return Integer.parseInt(nombreModulo.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE; // los sin número al final
        }
    }

    public List<TornilloConProductoDTO> obtenerDTOPorTiendaFamiliaYModulo(int tiendaId, String familia, String nombreModulo) {
        return tornilloRepository.findDTOByTiendaIdAndFamiliaAndNombreModulo(tiendaId, familia, nombreModulo);
    }

    // Un único DTO por tienda + producto
    public Optional<TornilloConProductoDTO> obtenerDTOPorTiendaYProducto(Integer tiendaId, Integer productoCodigo) {
        return tornilloRepository.obtenerDTOPorTiendaYProducto(tiendaId, productoCodigo);
    }

    /* ==========================
     * Actualizaciones existentes
     * ========================== */

    // Actualizar fecha de caducidad (YYYY-MM-DD)
    @Transactional
    public void actualizarFechaCaducidad(Long id, String fechaYmd) {
        if (fechaYmd == null || !fechaYmd.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use YYYY-MM-DD.");
        }
        LocalDate nueva = LocalDate.parse(fechaYmd);

        tornilloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tornillo no encontrado: " + id));

        tornilloRepository.updateFechaCaducidad(id, nueva);
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
                            int cf = Integer.compare(a.fila, b.fila); // fila asc
                            if (cf != 0) return cf;
                            boolean odd = (a.fila % 2) != 0;          // impar: izq→der; par: der→izq
                            return odd ? Integer.compare(a.columna, b.columna)
                                       : Integer.compare(b.columna, a.columna);
                        })
                )
                .collect(Collectors.toList());
    }

    /* ==========================
     * NUEVO: Editar tornillo (básico, sin tocar Producto)
     * ========================== */

    @Transactional
    public TornilloConProductoDTO editarTornilloBasico(Long id, UpdateTornilloBasicoRequest req) {
        TornilloEntity e = springTornillos.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tornillo no encontrado: " + id));

        // Posición
        if (req.getNombreModulo() != null && !req.getNombreModulo().isBlank()) {
            e.setNombreModulo(req.getNombreModulo());
        }
        if (req.getFila() != null) {
            e.setFila(req.getFila());
        }
        if (req.getColumna() != null) {
            e.setColumna(req.getColumna());
        }

        // Fecha de caducidad
        LocalDate nuevaCad = null;
        if (req.getFechaCaducidad() != null && !req.getFechaCaducidad().isBlank()) {
            if (!req.getFechaCaducidad().matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new IllegalArgumentException("Formato de fecha inválido. Use YYYY-MM-DD.");
            }
            nuevaCad = LocalDate.parse(req.getFechaCaducidad());
            e.setFechaCaducidad(nuevaCad);
        }

        // Recalcular retirada si nos pasan caducidadDias y tenemos fecha de caducidad
        if (nuevaCad != null && req.getCaducidadDias() != null) {
            e.setFechaRetirada(nuevaCad.minusDays(req.getCaducidadDias()));
        }

        springTornillos.save(e);

        // Devolver DTO completo (con nombre, imagen, familia, etc.)
        return springTornillos
                .findDTOByTiendaIdAndProductoCodigo(e.getTiendaId(), e.getProductoCodigo())
                .orElseThrow(() -> new IllegalStateException("No se pudo recuperar DTO tras editar tornillo"));
    }

    @Transactional
public void eliminarPorId(Long id) {
    // comprueba existencia para responder 404 si no existe
    if (!springTornillos.existsById(id)) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tornillo no encontrado: " + id);
    }
    springTornillos.deleteById(id);
}

@Transactional
public TornilloConProductoDTO crearTornilloBasico(CreateTornilloRequest req) {
    // 1) Validación mínima
    if (req.getTiendaId() == null ||
        req.getProductoCodigo() == null ||
        req.getNombreModulo() == null || req.getNombreModulo().isBlank() ||
        req.getFila() == null ||
        req.getColumna() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan campos obligatorios");
    }

    // 2) Parseo/validación fecha
    LocalDate cad = null;
    if (req.getFechaCaducidad() != null && !req.getFechaCaducidad().isBlank()) {
        if (!req.getFechaCaducidad().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha inválido (YYYY-MM-DD)");
        }
        try {
            cad = LocalDate.parse(req.getFechaCaducidad());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo parsear la fecha de caducidad");
        }
    }

    // 3) Calcular retirada si procede
    LocalDate retirada = null;
    if (cad != null && req.getCaducidadDias() != null) {
        if (req.getCaducidadDias() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "caducidadDias no puede ser negativo");
        }
        retirada = cad.minusDays(req.getCaducidadDias());
    }

    // 4) (Opcional) Comprobación de colisión en celda dentro del módulo
    //    Si tu SpringData tiene algo como existsBy..., úsalo. Si no, listamos y comprobamos en memoria:
    var existentes = tornilloRepository.findByTiendaIdAndFamiliaAndNombreModulo(
            req.getTiendaId(),
            // familia no está en TornilloEntity; la proyección la saca de Producto.
            // Para no tocar Productos ahora, no filtramos por familia aquí:
            "", 
            req.getNombreModulo()
    );
    // Si la firma de arriba no encaja, puedes omitir esta comprobación o adaptarla a tu repo.
    // De forma segura, la omito para no romper nada.

    // 5) Construir entidad y guardar
    TornilloEntity e = new TornilloEntity();
    e.setTiendaId(req.getTiendaId());
    e.setProductoCodigo(req.getProductoCodigo());
    e.setNombreModulo(req.getNombreModulo());
    e.setFila(req.getFila());
    e.setColumna(req.getColumna());
    e.setFechaCaducidad(cad);
    e.setFechaRetirada(retirada);

    springTornillos.save(e);

    // 6) Devolver DTO “rico” si existe proyección; si no, fallback mínimo
    var dtoOpt = springTornillos.findDTOByTiendaIdAndProductoCodigo(e.getTiendaId(), e.getProductoCodigo());
    if (dtoOpt.isPresent()) return dtoOpt.get();

    return new TornilloConProductoDTO(
            e.getId(),
            e.getProductoCodigo(),
            e.getTiendaId(),
            e.getFechaCaducidad() != null ? e.getFechaCaducidad().toString() : null,
            e.getFechaRetirada() != null ? e.getFechaRetirada().toString() : null,
            e.getNombreModulo(),
            e.getFila(),
            e.getColumna(),
            null,    // nombre
            null     // imagenUrl
    );
}
    
}
