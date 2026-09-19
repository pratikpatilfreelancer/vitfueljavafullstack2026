package com.library.libraryservice.exception;

/** Thrown when the User Service cannot be reached during authentication. */
public class RemoteServiceException extends RuntimeException {
    public RemoteServiceException(String message) {
        super(message);
    }
}
