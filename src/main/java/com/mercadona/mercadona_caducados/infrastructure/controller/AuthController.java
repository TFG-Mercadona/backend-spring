package com.mercadona.mercadona_caducados.infrastructure.controller;

import com.mercadona.mercadona_caducados.application.dto.LoginRequest;
import com.mercadona.mercadona_caducados.application.dto.LoginResponse;
import com.mercadona.mercadona_caducados.application.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }
}
