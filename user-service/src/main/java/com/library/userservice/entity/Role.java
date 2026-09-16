package com.library.userservice.entity;

/**
 * Fixed set of roles supported by the system.
 * STUDENT is assigned automatically on public registration.
 * LIBRARIAN accounts are created manually (e.g. directly in the database) for demonstration.
 */
public enum Role {
    STUDENT,
    LIBRARIAN
}
