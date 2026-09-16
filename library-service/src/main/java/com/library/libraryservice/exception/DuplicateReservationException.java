package com.library.libraryservice.exception;

/** Thrown when a student already has an active (PENDING/APPROVED/ISSUED) reservation for the same book. */
public class DuplicateReservationException extends RuntimeException {
    public DuplicateReservationException(String message) {
        super(message);
    }
}
