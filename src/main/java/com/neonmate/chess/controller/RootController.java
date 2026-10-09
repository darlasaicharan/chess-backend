package com.neonmate.chess.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping({"", "/", "/health"})
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "NEONMATE CHESS Backend API",
                "version", "1.0.0",
                "documentation", "/swagger-ui.html",
                "timestamp", Instant.now().toString()
        ));
    }
}
