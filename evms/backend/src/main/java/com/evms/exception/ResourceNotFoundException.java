package com.evms.exception;

/**
 * Exception thrown when a requested resource is not found.
 * <p>
 * Maps to HTTP 404 Not Found in the global exception handler.
 * Example: "Voucher not found."
 *
 * @author EVMS Team
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified message.
     *
     * @param message the error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
