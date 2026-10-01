package com.internships.controller;

import com.internships.model.User;
import com.internships.service.AuthService;
import com.internships.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login and registration APIs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        User user = authService.login(email, password);
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("refId", user.getRefId());
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody User user) {
        User created = authService.register(user);
        Map<String, Object> response = new HashMap<>();
        response.put("userId", created.getUserId());
        response.put("email", created.getEmail());
        response.put("role", created.getRole());
        response.put("refId", created.getRefId());
        response.put("message", "Registration successful");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
