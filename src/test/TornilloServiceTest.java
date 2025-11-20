package com.mercadona.mercadona_caducados.application;

import com.mercadona.mercadona_caducados.domain.model.Tornillo;
import com.mercadona.mercadona_caducados.domain.repository.TornilloRepository;
import com.mercadona.mercadona_caducados.infrastructure.persistence.SpringDataTornilloRepository;
import com.mercadona.mercadona_caducados.infrastructure.persistence.SpringDataCambioCaducidadRepository;
import com.mercadona.mercadona_caducados.infrastructure.persistence.TornilloEntity;
import com.mercadona.mercadona_caducados.infrastructure.persistence.TornilloCambioCaducidadEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TornilloServiceTest {

    @Mock
    private TornilloRepository tornilloRepository;
    
    @Mock
    private SpringDataTornilloRepository springTornillos;
    
    @Mock
    private SpringDataCambioCaducidadRepository cambiosRepo;

    private TornilloService tornilloService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tornilloService = new TornilloService(tornilloRepository, springTornillos, cambiosRepo);
    }

    @Test
    public void testActualizarFechaCaducidad_Correcta() {
        // Arrange
        Long id = 1L;
        String nuevaFecha = "2023-12-01";
        
        TornilloEntity tornilloEntity = new TornilloEntity();
        tornilloEntity.setId(id);
        tornilloEntity.setFechaCaducidad(LocalDate.of(2023, 11, 01));
        
        when(springTornillos.findById(id)).thenReturn(Optional.of(tornilloEntity));
        
        // Act
        tornilloService.actualizarFechaCaducidad(id, nuevaFecha);
        
        // Assert
        assertEquals(LocalDate.of(2023, 12, 1), tornilloEntity.getFechaCaducidad());
        verify(springTornillos, times(1)).save(tornilloEntity);  // Ensure save was called
        verify(cambiosRepo, times(1)).save(any(TornilloCambioCaducidadEntity.class));  // Ensure audit log was created
    }

    @Test
    public void testActualizarFechaCaducidad_Invalida() {
        // Arrange
        Long id = 1L;
        String fechaInvalida = "2023-13-01";  // Invalid date
        
        TornilloEntity tornilloEntity = new TornilloEntity();
        tornilloEntity.setId(id);
        tornilloEntity.setFechaCaducidad(LocalDate.of(2023, 11, 01));
        
        when(springTornillos.findById(id)).thenReturn(Optional.of(tornilloEntity));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tornilloService.actualizarFechaCaducidad(id, fechaInvalida);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void testCrearTornilloBasico_CreacionCorrecta() {
        // Arrange
        CreateTornilloRequest request = new CreateTornilloRequest();
        request.setTiendaId(1);
        request.setProductoCodigo(1001);
        request.setNombreModulo("Modulo 1");
        request.setFila(1);
        request.setColumna(1);
        request.setFechaCaducidad("2023-12-01");
        request.setCaducidadDias(30);
        
        TornilloEntity tornilloEntity = new TornilloEntity();
        tornilloEntity.setTiendaId(1);
        tornilloEntity.setProductoCodigo(1001);
        tornilloEntity.setNombreModulo("Modulo 1");
        tornilloEntity.setFila(1);
        tornilloEntity.setColumna(1);
        tornilloEntity.setFechaCaducidad(LocalDate.parse("2023-12-01"));
        tornilloEntity.setFechaRetirada(LocalDate.parse("2023-11-01"));
        
        when(springTornillos.save(any(TornilloEntity.class))).thenReturn(tornilloEntity);
        
        // Act
        TornilloConProductoDTO dto = tornilloService.crearTornilloBasico(request);

        // Assert
        assertNotNull(dto);
        assertEquals(1001, dto.getProductoCodigo());
        assertEquals("Modulo 1", dto.getNombreModulo());
        assertEquals(LocalDate.parse("2023-12-01"), dto.getFechaCaducidad());
    }

    @Test
    public void testCrearTornilloBasico_FaltanCampos() {
        // Arrange
        CreateTornilloRequest request = new CreateTornilloRequest();
        request.setTiendaId(1);  // Missing fields
        
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tornilloService.crearTornilloBasico(request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
