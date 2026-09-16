package com.evms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration for the EVMS API.
 * <p>
 * Allows the React frontend (running on a different port, e.g., localhost:5173)
 * to make API requests to this backend (localhost:4000).
 * <p>
 * This replaces the {@code cors()} Express middleware from the Node.js backend.
 * The configuration allows all origins, methods, and headers — matching the
 * permissive CORS policy of the original backend.
 *
 * @author EVMS Team
 */
@Configuration
public class CorsConfig {

    /**
     * Creates a CORS filter bean that permits cross-origin requests.
     * <p>
     * Configuration:
     * <ul>
     *   <li>All origins allowed (for development)</li>
     *   <li>All standard HTTP methods allowed</li>
     *   <li>Authorization and Content-Type headers allowed</li>
     *   <li>Credentials (cookies) allowed</li>
     *   <li>Cross-origin resource policy allows image loading from frontend</li>
     * </ul>
     *
     * @return a configured CorsFilter bean
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow all origins (matches Express cors() default)
        config.setAllowedOriginPatterns(List.of("*"));

        // Allow all common HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow Authorization header (for JWT) and Content-Type
        config.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept",
                "Origin", "X-Requested-With"
        ));

        // Allow credentials
        config.setAllowCredentials(true);

        // Expose headers the frontend might need
        config.setExposedHeaders(List.of("Authorization"));

        // Apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
