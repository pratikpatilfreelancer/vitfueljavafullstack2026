package com.library.libraryservice.exception;

/** Thrown when a student tries to reserve a book with zero available copies. */
public class BookUnavailableException extends RuntimeException {
    public BookUnavailableException(String message) {
        super(message);
    }
}
