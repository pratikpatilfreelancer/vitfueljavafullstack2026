package com.library.userservice.controller;

import com.library.userservice.dto.LoginRequest;
import com.library.userservice.dto.RegisterRequest;
import com.library.userservice.dto.UpdateUserRequest;
import com.library.userservice.dto.UserResponse;
import com.library.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for registration, login and user/profile management.
 * Role-based restrictions (e.g. listing/deleting users) are enforced in SecurityConfig;
 * ownership checks (a student may only view/edit their own profile) are enforced in the service layer.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request.getEmail(), request.getPassword()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(userService.getUserById(id, authentication));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        // Restricted to LIBRARIAN by SecurityConfig.
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateUserRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(userService.updateUser(id, request, authentication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Restricted to LIBRARIAN by SecurityConfig.
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
