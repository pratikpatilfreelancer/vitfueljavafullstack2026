package com.evms.config;

import com.evms.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

/**
 * Spring Security configuration for the EVMS application.
 * <p>
 * Configures the application as a stateless REST API:
 * <ul>
 *   <li>CSRF is disabled (not needed for stateless JWT-based auth)</li>
 *   <li>Session management set to STATELESS (no server-side sessions)</li>
 *   <li>Public endpoints: {@code /api/auth/login}, {@code /api/health}, {@code /uploads/**}</li>
 *   <li>All other {@code /api/**} endpoints require authentication via JWT</li>
 *   <li>JWT filter is registered before the default UsernamePasswordAuthenticationFilter</li>
 * </ul>
 * <p>
 * Also defines the {@link BCryptPasswordEncoder} bean used for password hashing.
 *
 * @author EVMS Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** JWT authentication filter injected by Spring. */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructs the SecurityConfig with the JWT filter dependency.
     *
     * @param jwtAuthenticationFilter the JWT filter component
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configures the HTTP security filter chain.
     * <p>
     * Defines which endpoints are public, which require authentication,
     * and how authentication errors are handled.
     *
     * @param http the HttpSecurity builder
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — stateless REST API, no cookies/sessions
                .csrf(csrf -> csrf.disable())

                // Stateless session management — no server-side sessions
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Endpoint authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication required)
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        // All other API endpoints require authentication
                        .requestMatchers("/api/**").authenticated()
                        // Allow everything else (e.g., static resources)
                        .anyRequest().permitAll()
                )

                // Custom 401 response when authentication fails
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            new ObjectMapper().writeValue(
                                    response.getOutputStream(),
                                    Map.of("error", "Missing authentication token.")
                            );
                        })
                )

                // Register JWT filter before Spring's default auth filter
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provides a BCrypt password encoder bean.
     * <p>
     * BCrypt is used for hashing passwords on registration/seeding and
     * verifying passwords during login — same algorithm as the Node.js
     * backend's {@code bcryptjs} library, ensuring hash compatibility.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
