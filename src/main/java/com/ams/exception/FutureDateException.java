package com.ams.exception;

/**
 * Thrown when attendance is attempted for a future date.
 */
public class FutureDateException extends RuntimeException {

    public FutureDateException(String message) {
        super(message);
    }
}
