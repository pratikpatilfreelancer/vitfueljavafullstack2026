package com.evms.exception;

/**
 * Exception thrown when a user attempts an action they don't have permission for.
 * <p>
 * Maps to HTTP 403 Forbidden in the global exception handler.
 * Examples: "You can only view your own vouchers.", "You do not have permission to perform this action."
 *
 * @author EVMS Team
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * Constructs a new AccessDeniedException with the specified message.
     *
     * @param message the error message
     */
    public AccessDeniedException(String message) {
        super(message);
    }
}
