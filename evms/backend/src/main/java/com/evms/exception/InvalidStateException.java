package com.evms.exception;

/**
 * Exception thrown when an operation violates the voucher state machine.
 * <p>
 * Maps to HTTP 409 Conflict in the global exception handler.
 * Examples: "Only Draft vouchers can be edited.", "Only vouchers pending approval can be approved."
 *
 * @author EVMS Team
 */
public class InvalidStateException extends RuntimeException {

    /**
     * Constructs a new InvalidStateException with the specified message.
     *
     * @param message the error message
     */
    public InvalidStateException(String message) {
        super(message);
    }
}
