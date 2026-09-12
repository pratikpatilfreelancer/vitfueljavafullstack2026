package com.bakeryvault.exceptions;

public class ItemNotFoundException extends BakeryException {
    public ItemNotFoundException(String itemId) {
        super("No item found with id: " + itemId);
    }
}
