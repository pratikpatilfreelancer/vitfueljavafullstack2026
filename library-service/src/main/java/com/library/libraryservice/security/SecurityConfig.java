package com.library.libraryservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/**
 * Security configuration for the Library Service.
 *
 * Unlike the User Service, there is no local UserDetailsService: authentication
 * is delegated to {@link RemoteAuthenticationProvider}, which calls the User
 * Service over REST. Authorization rules below still enforce STUDENT vs
 * LIBRARIAN permissions locally, per the requirements.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RemoteAuthenticationProvider remoteAuthenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(remoteAuthenticationProvider));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public catalog browsing - no login required to search/view books.
                        .requestMatchers(HttpMethod.GET, "/api/books", "/api/books/**", "/api/books/search").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/books").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("LIBRARIAN")

                        .requestMatchers(HttpMethod.POST, "/api/reservations").hasRole("STUDENT")
                        // Cancel is open to either role - ReservationService enforces that a STUDENT
                        // may only cancel their own reservation, while a LIBRARIAN may cancel any.
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/*/cancel").hasAnyRole("STUDENT", "LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/*/approve").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/reservations").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/reservations/user/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/borrowings/issue").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/api/borrowings/*/return").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/borrowings/**").hasRole("LIBRARIAN")

                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {})
                .authenticationManager(authenticationManager());

        return http.build();
    }
}
