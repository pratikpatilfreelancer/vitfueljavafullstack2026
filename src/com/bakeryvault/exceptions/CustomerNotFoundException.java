package com.bakeryvault.exceptions;

public class CustomerNotFoundException extends BakeryException {
    public CustomerNotFoundException(String customerId) {
        super("No customer found with id: " + customerId);
    }
}
