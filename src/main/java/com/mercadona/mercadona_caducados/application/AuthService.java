package com.mercadona.mercadona_caducados.application;

import com.mercadona.mercadona_caducados.application.dto.LoginRequest;
import com.mercadona.mercadona_caducados.application.dto.LoginResponse;
import com.mercadona.mercadona_caducados.domain.repository.TiendaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final TiendaRepository tiendaRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(TiendaRepository tiendaRepository) {
        this.tiendaRepository = tiendaRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public LoginResponse login(LoginRequest request) {
        return tiendaRepository.findById(request.id())
            .map(tienda -> {
                if (passwordEncoder.matches(request.password(), tienda.getPasswordHashed())) {
                    return new LoginResponse(true, "Login exitoso");
                } else {
                    return new LoginResponse(false, "Contraseña incorrecta");
                }
            })
            .orElse(new LoginResponse(false, "Tienda no encontrada"));
    }
}
