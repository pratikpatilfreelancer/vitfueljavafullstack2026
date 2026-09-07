package com.smarthire;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SqlConnectionController {

    private final JdbcTemplate jdbcTemplate;

    public SqlConnectionController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        return Map.of(
            "status", "OK",
            "database", one != null && one == 1 ? "CONNECTED" : "ERROR"
        );
    }
}