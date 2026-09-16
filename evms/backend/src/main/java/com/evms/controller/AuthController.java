package com.evms.controller;

import com.evms.dto.request.LoginRequest;
import com.evms.dto.response.LoginResponse;
import com.evms.security.UserPrincipal;
import com.evms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication endpoints.
 * <p>
 * Handles user login and "get current user" operations.
 * Replaces the Node.js {@code routes/auth.js} router.
 * <p>
 * Endpoints:
 * <ul>
 *   <li>{@code POST /api/auth/login} — Authenticate with email + password</li>
 *   <li>{@code GET /api/auth/me} — Get the currently authenticated user's profile</li>
 * </ul>
 *
 * @author EVMS Team
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** Authentication service for login logic. */
    private final AuthService authService;

    /**
     * Constructs the AuthController with the AuthService dependency.
     *
     * @param authService the authentication service
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticates a user with email and password credentials.
     * <p>
     * This is a public endpoint — no JWT required.
     * Returns a JWT token and user profile on success.
     *
     * @param request the login request containing email and password
     * @return JSON response: {@code { "token": "...", "user": { id, name, email, role, department } }}
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Returns the currently authenticated user's profile from the JWT token.
     * <p>
     * Requires a valid JWT token in the Authorization header.
     * The user profile is extracted from the JWT claims (no database lookup).
     *
     * @param principal the authenticated user's identity (from JWT)
     * @return JSON response: {@code { "user": { id, name, email, role, department } }}
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {
        // Build user payload matching the Node.js /auth/me response shape
        Map<String, Object> user = Map.of(
                "id", principal.getId(),
                "name", principal.getName(),
                "email", principal.getEmail(),
                "role", principal.getRole(),
                "department", principal.getDepartment() != null ? principal.getDepartment() : ""
        );
        return ResponseEntity.ok(Map.of("user", user));
    }
}
