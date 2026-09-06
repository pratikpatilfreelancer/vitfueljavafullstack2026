package com.evms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * REST controller for the health check endpoint.
 * <p>
 * Provides a simple endpoint to verify the API is running.
 * Matches the original Node.js {@code GET /api/health} endpoint.
 *
 * @author EVMS Team
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * Health check endpoint — returns the API status and current time.
     * <p>
     * Response: {@code { "status": "ok", "time": "2026-08-28T06:30:00Z" }}
     *
     * @return JSON with status and timestamp
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "time", Instant.now().toString()
        ));
    }
}
