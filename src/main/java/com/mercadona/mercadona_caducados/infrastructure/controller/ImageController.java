package com.mercadona.mercadona_caducados.infrastructure.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/images/productos")
public class ImageController {

    private final Path imageDir = Paths.get("src/main/resources/static/images/productos");

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path imagePath = imageDir.resolve(filename);
            if (!Files.exists(imagePath)) {
                imagePath = imageDir.resolve("imageNotFound.png");
            }

            Resource file = new UrlResource(imagePath.toUri());
            if (file.exists() || file.isReadable()) {
                String contentType = Files.probeContentType(imagePath);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType != null ? contentType : "image/png"))
                        .body(file);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
