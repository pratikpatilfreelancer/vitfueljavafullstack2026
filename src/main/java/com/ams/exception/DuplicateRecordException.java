package com.ams.exception;

/**
 * Thrown when a duplicate record is detected (e.g. duplicate attendance).
 */
public class DuplicateRecordException extends RuntimeException {

    public DuplicateRecordException(String message) {
        super(message);
    }
}
