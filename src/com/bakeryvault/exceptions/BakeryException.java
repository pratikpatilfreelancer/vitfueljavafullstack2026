package com.bakeryvault.exceptions;

/**
 * Base checked exception for the BakeryVault domain. All user-defined
 * exceptions in this project extend this class, so callers can choose
 * to catch broadly (BakeryException) or narrowly (a specific subtype)
 * depending on their needs.
 */
public class BakeryException extends Exception {
    public BakeryException(String message) {
        super(message);
    }

    public BakeryException(String message, Throwable cause) {
        super(message, cause);
    }
}
