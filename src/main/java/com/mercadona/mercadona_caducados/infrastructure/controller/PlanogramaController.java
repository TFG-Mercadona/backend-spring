package com.mercadona.mercadona_caducados.infrastructure.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.Locale;

@RestController
@RequestMapping("/api/planogramas")
public class PlanogramaController {

    @GetMapping("/{familia}")
    public ResponseEntity<Resource> verPlanograma(@PathVariable String familia) {
        // Intentos de nombre de archivo en este orden:
        //  1) Exacto: "Lácteos Mural.pdf"
        //  2) Sin acentos: "Lacteos Mural.pdf"
        //  3) Slug básico: "lacteos-mural.pdf"
        String exact = "static/planogramas/" + familia + ".pdf";
        String sinAcentos = "static/planogramas/" + quitarAcentos(familia) + ".pdf";
        String slug = "static/planogramas/" + slugify(familia) + ".pdf";

        Resource res = pickFirstExisting(exact, sinAcentos, slug);
        if (res == null || !res.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + res.getFilename() + "\"");
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    private Resource pickFirstExisting(String... classpathNames) {
        for (String p : classpathNames) {
            Resource r = new ClassPathResource(p);
            if (r.exists()) return r;
        }
        return null;
    }

    private String quitarAcentos(String s) {
        String norm = Normalizer.normalize(s, Normalizer.Form.NFD);
        return norm.replaceAll("\\p{M}+", "");
    }

    private String slugify(String s) {
        String t = quitarAcentos(s).toLowerCase(Locale.ROOT).trim();
        t = t.replaceAll("[^a-z0-9]+", "-");
        t = t.replaceAll("(^-|-$)", "");
        return t;
    }
}
