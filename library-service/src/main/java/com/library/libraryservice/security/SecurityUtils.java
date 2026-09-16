package com.library.libraryservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/** Small helper for reading the current request's resolved identity in controllers. */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static AuthenticatedUser currentUser(Authentication authentication) {
        return (AuthenticatedUser) authentication.getDetails();
    }

    public static boolean isLibrarian(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_LIBRARIAN"));
    }
}
