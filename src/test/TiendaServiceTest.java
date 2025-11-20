package com.mercadona.mercadona_caducados.application;

import com.mercadona.mercadona_caducados.domain.model.Tienda;
import com.mercadona.mercadona_caducados.domain.repository.TiendaRepository;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TiendaServiceTest {

    @Mock
    private TiendaRepository tiendaRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private TiendaService tiendaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tiendaService = new TiendaService(tiendaRepository, passwordEncoder);
    }

    @Test
    void testLogin_Correcto() {
        Tienda tienda = new Tienda(3718, "Mercadona Av. Madrid", "hash123");
        
        when(tiendaRepository.findById(3718)).thenReturn(Optional.of(tienda));
        when(passwordEncoder.matches("pwd123", "hash123")).thenReturn(true);

        Tienda result = tiendaService.login(3718, "pwd123");

        assertNotNull(result);
        assertEquals(3718, result.getId());
    }

    @Test
    void testLogin_PasswordIncorrecto() {
        Tienda tienda = new Tienda(3718, "Mercadona Av. Madrid", "hash123");

        when(tiendaRepository.findById(3718)).thenReturn(Optional.of(tienda));
        when(passwordEncoder.matches("badpass", "hash123")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> {
            tiendaService.login(3718, "badpass");
        });
    }

    @Test
    void testLogin_TiendaNoExiste() {
        when(tiendaRepository.findById(9999)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            tiendaService.login(9999, "pwd");
        });
    }
}
