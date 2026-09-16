package com.pockettrack.exception;

public class InsufficientSavingsException extends RuntimeException {
    public InsufficientSavingsException(String message) {
        super(message);
    }
}
