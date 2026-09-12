package com.capgemini.apartment_maintenance.exception;

// thrown when a request is well-formed but violates a business rule
// e.g. resolving a complaint with no staff assigned — mapped to 400 later
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}