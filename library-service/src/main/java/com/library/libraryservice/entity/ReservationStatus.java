package com.library.libraryservice.entity;

/**
 * Lifecycle of a reservation:
 * PENDING (just created) -> APPROVED (librarian approved) -> ISSUED (book handed over)
 * -> COMPLETED (book returned), or CANCELLED at any point before ISSUED.
 */
public enum ReservationStatus {
    PENDING,
    APPROVED,
    CANCELLED,
    ISSUED,
    COMPLETED
}
