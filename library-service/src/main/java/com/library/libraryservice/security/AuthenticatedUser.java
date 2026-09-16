package com.library.libraryservice.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Extra identity details attached to the Spring Security Authentication (via
 * Authentication#getDetails()) once a request has been verified against the
 * User Service. Controllers read this instead of trusting a userId supplied
 * by the client.
 */
@Getter
@AllArgsConstructor
public class AuthenticatedUser {
    private final Long userId;
    private final String name;
    private final String email;
    private final String role;
}
