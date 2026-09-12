package com.evms.exception;

/**
 * Exception thrown when request validation fails.
 * <p>
 * Maps to HTTP 400 Bad Request in the global exception handler.
 * Examples: "Amount must be greater than zero.", "Email and password are required."
 *
 * @author EVMS Team
 */
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new BadRequestException with the specified message.
     *
     * @param message the error message
     */
    public BadRequestException(String message) {
        super(message);
    }
}
