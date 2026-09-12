package com.bakeryvault.exceptions;

public class InvalidSKUException extends BakeryException {
    public InvalidSKUException(String sku) {
        super("Invalid SKU format: '" + sku + "'. Expected 6-12 alphanumeric characters.");
    }
}
