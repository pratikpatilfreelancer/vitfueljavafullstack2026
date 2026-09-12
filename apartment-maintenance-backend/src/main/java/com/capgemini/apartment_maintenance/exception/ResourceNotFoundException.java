package com.capgemini.apartment_maintenance.exception;

// thrown when a lookup by ID fails — mapped to 404 in the global handler (next step)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}