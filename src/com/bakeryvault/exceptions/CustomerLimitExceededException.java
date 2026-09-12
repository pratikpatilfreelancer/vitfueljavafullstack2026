package com.bakeryvault.exceptions;

public class CustomerLimitExceededException extends BakeryException {
    public CustomerLimitExceededException(String customerId, int limit) {
        super("Customer [" + customerId + "] has reached the purchase limit of " + limit + " items.");
    }
}
