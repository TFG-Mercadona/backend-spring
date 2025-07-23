package com.mercadona.mercadona_caducados.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        String raw = "pass3718";
        String hashed = new BCryptPasswordEncoder().encode(raw);
        System.out.println("Hashed: " + hashed);
    }
}
