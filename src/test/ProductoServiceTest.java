package com.mercadona.mercadona_caducados.application;

import com.mercadona.mercadona_caducados.domain.model.Producto;
import com.mercadona.mercadona_caducados.domain.repository.ProductoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    private ProductoService productoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        productoService = new ProductoService(productoRepository);
    }

    @Test
    public void testObtenerTodos_ReturnsProductos() {
        // Arrange
        Producto p1 = new Producto(1001, "Leche Entera", "Lácteos", 10);
        Producto p2 = new Producto(1002, "Yogur Natural", "Lácteos", 20);

        when(productoRepository.findAll()).thenReturn(List.of(p1, p2));

        // Act
        List<Producto> productos = productoService.obtenerTodos();

        // Assert
        assertEquals(2, productos.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerPorCodigo_ProductoExiste() {
        Producto p = new Producto(2001, "Jamón Cocido", "Charcutería", 15);

        when(productoRepository.findByCodigo(2001)).thenReturn(Optional.of(p));

        Optional<Producto> result = productoService.obtenerPorCodigo(2001);

        assertTrue(result.isPresent());
        assertEquals("Jamón Cocido", result.get().getNombre());
    }

    @Test
    public void testObtenerPorCodigo_NoExiste() {
        when(productoRepository.findByCodigo(9999)).thenReturn(Optional.empty());

        Optional<Producto> result = productoService.obtenerPorCodigo(9999);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testObtenerPorFamilia_ReturnsCorrectos() {
        Producto p1 = new Producto(3001, "Queso Curado", "Lácteos", 30);
        Producto p2 = new Producto(3002, "Leche Semidesnatada", "Lácteos", 10);

        when(productoRepository.findByFamilia("Lácteos")).thenReturn(List.of(p1, p2));

        List<Producto> result = productoService.obtenerPorFamilia("Lácteos");

        assertEquals(2, result.size());
        assertEquals("Queso Curado", result.get(0).getNombre());
    }
}
