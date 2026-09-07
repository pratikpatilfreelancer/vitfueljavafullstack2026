package com.smarthire;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final JdbcTemplate jdbcTemplate;

    public UserController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest request) {

        String sql = """
            INSERT INTO users (username, password, role, email, full_name)
            VALUES (?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(
            sql,
            request.username,
            request.password,
            request.role,
            request.email,
            request.fullName
        );

        Integer userId = jdbcTemplate.queryForObject(
            "SELECT user_id FROM users WHERE username = ?",
            Integer.class,
            request.username
        );

        return Map.of(
            "status", "SUCCESS",
            "userId", userId,
            "message", "User saved in MySQL"
        );
    }

    public static class RegisterRequest {

        public String username;
        public String password;
        public String role;
        public String email;
        public String fullName;
    }
}