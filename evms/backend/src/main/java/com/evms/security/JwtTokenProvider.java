package com.evms.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility component for generating and validating JWT (JSON Web Tokens).
 * <p>
 * Tokens are signed with HS256 using a configurable secret key.
 * The token payload contains user identity information (id, name, email,
 * role, department) — matching the exact payload shape used by the
 * original Node.js backend so the React frontend can decode it unchanged.
 *
 * @author EVMS Team
 */
@Component
public class JwtTokenProvider {

    /** Secret key used to sign and verify JWT tokens. */
    private final SecretKey secretKey;

    /** Token expiration time in milliseconds (default: 8 hours). */
    @Value("${jwt.expiration-ms:28800000}")
    private long expirationMs;

    /**
     * Constructs the JwtTokenProvider with the configured secret key.
     *
     * @param jwtSecret the secret string from application.properties
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret) {
        // Ensure the key is at least 256 bits for HS256
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // Pad to 32 bytes if secret is too short (dev convenience)
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            keyBytes = padded;
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for the given user.
     * <p>
     * The token payload includes: id, name, email, role, department.
     * This matches the exact claims the Node.js backend used, so the
     * React frontend's token decoding logic works without changes.
     *
     * @param id         user's unique ID
     * @param name       user's full name
     * @param email      user's email address
     * @param role       user's role (EMPLOYEE, DIRECTOR, ACCOUNTS)
     * @param department user's department
     * @return signed JWT token string
     */
    public String generateToken(Long id, String name, String email, String role, String department) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        // Build claims map matching the original Node.js payload shape
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("name", name);
        claims.put("email", email);
        claims.put("role", role);
        claims.put("department", department);

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(id))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts the user ID from a JWT token.
     *
     * @param token the JWT token string
     * @return the user ID stored in the token's subject claim
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("id", Long.class);
    }

    /**
     * Extracts the user's name from a JWT token.
     *
     * @param token the JWT token string
     * @return the user's name
     */
    public String getNameFromToken(String token) {
        return parseToken(token).get("name", String.class);
    }

    /**
     * Extracts the user's email from a JWT token.
     *
     * @param token the JWT token string
     * @return the user's email
     */
    public String getEmailFromToken(String token) {
        return parseToken(token).get("email", String.class);
    }

    /**
     * Extracts the user's role from a JWT token.
     *
     * @param token the JWT token string
     * @return the user's role as a string
     */
    public String getRoleFromToken(String token) {
        return parseToken(token).get("role", String.class);
    }

    /**
     * Extracts the user's department from a JWT token.
     *
     * @param token the JWT token string
     * @return the user's department
     */
    public String getDepartmentFromToken(String token) {
        return parseToken(token).get("department", String.class);
    }

    /**
     * Validates a JWT token by attempting to parse it.
     *
     * @param token the JWT token string to validate
     * @return {@code true} if the token is valid and not expired
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Parses a JWT token and returns its claims.
     *
     * @param token the JWT token string
     * @return the parsed claims
     * @throws JwtException if the token is invalid, expired, or tampered with
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
