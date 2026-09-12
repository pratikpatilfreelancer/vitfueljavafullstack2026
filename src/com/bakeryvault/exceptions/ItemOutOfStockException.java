package com.bakeryvault.exceptions;

public class ItemOutOfStockException extends BakeryException {
    public ItemOutOfStockException(String itemId) {
        super("Item [" + itemId + "] is currently out of stock.");
    }
}
