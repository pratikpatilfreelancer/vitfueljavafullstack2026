package com.evms.service;

import com.evms.dto.request.LoginRequest;
import com.evms.dto.response.LoginResponse;
import com.evms.exception.BadRequestException;
import com.evms.model.User;
import com.evms.repository.UserRepository;
import com.evms.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service handling authentication operations.
 * <p>
 * Provides login functionality: validates email and password credentials
 * against the database, and generates a JWT token on successful login.
 * <p>
 * Replaces the logic in the Node.js {@code routes/auth.js} file.
 *
 * @author EVMS Team
 */
@Service
public class AuthService {

    /** Repository for user data access. */
    private final UserRepository userRepository;

    /** BCrypt password encoder for verifying hashed passwords. */
    private final PasswordEncoder passwordEncoder;

    /** JWT utility for generating authentication tokens. */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs the AuthService with required dependencies.
     *
     * @param userRepository   the user data access repository
     * @param passwordEncoder  the BCrypt password encoder
     * @param jwtTokenProvider the JWT token generator/validator
     */
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Authenticates a user with email and password credentials.
     * <p>
     * Workflow:
     * <ol>
     *   <li>Looks up the user by email (case-insensitive, trimmed)</li>
     *   <li>Verifies the password against the stored BCrypt hash</li>
     *   <li>Generates a JWT token with the user's identity claims</li>
     *   <li>Returns the token and user profile</li>
     * </ol>
     *
     * @param request the login request containing email and password
     * @return a LoginResponse with the JWT token and user payload
     * @throws BadRequestException if credentials are invalid
     */
    public LoginResponse login(LoginRequest request) {
        // Normalise email to lowercase and trim whitespace
        String email = request.getEmail().toLowerCase().trim();

        // Look up user by email — return same error for both "not found" and
        // "wrong password" to prevent email enumeration attacks
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password."));

        // Verify password against stored BCrypt hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password.");
        }

        // Generate JWT token with user identity claims
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getDepartment()
        );

        // Build response payload matching the Node.js backend's format:
        // { token: "...", user: { id, name, email, role, department } }
        LoginResponse.UserPayload userPayload = new LoginResponse.UserPayload(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getDepartment()
        );

        return new LoginResponse(token, userPayload);
    }
}
