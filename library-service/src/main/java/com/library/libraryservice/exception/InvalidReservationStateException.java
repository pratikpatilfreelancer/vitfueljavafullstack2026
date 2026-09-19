package com.library.libraryservice.exception;

/** Thrown when an operation is attempted on a reservation/borrowing that is in the wrong status for it. */
public class InvalidReservationStateException extends RuntimeException {
    public InvalidReservationStateException(String message) {
        super(message);
    }
}
