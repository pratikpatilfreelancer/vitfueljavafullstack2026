package com.library.libraryservice.exception;

/** Thrown when a librarian tries to add a book whose ISBN already exists in the catalog. */
public class DuplicateIsbnException extends RuntimeException {
    public DuplicateIsbnException(String message) {
        super(message);
    }
}
