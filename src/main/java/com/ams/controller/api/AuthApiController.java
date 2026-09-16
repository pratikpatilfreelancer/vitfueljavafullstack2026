package com.ams.controller.api;

import com.ams.entity.User;
import com.ams.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthApiController(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            
            User user = userRepository.findByUsername(username).orElseThrow();

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Login successful",
                    "username", user.getUsername(),
                    "fullName", user.getFullName(),
                    "role", user.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", "ERROR",
                    "message", "Invalid username or password"
            ));
        }
    }
}
