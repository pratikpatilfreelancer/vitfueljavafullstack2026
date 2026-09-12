package com.bakeryvault.service;

import com.bakeryvault.exceptions.BakeryException;

/**
 * Runnable task representing one customer's attempt to purchase one item.
 * Used to demonstrate Thread/Runnable and the need for synchronization:
 * when several customers race to buy the SAME item, only one should
 * succeed, and BakeryService's synchronized purchaseItem() method makes
 * that guarantee.
 */
public class CheckoutTask implements Runnable {

    private final BakeryService bakeryService;
    private final String itemId;
    private final String customerId;

    public CheckoutTask(BakeryService bakeryService, String itemId, String customerId) {
        this.bakeryService = bakeryService;
        this.itemId = itemId;
        this.customerId = customerId;
    }

    @Override
    public void run() {
        try {
            bakeryService.purchaseItem(itemId, customerId);
            System.out.println(Thread.currentThread().getName()
                    + " -> SUCCESS: customer " + customerId + " purchased " + itemId);
        } catch (BakeryException e) {
            System.out.println(Thread.currentThread().getName()
                    + " -> FAILED: customer " + customerId + " could not purchase " + itemId
                    + " (" + e.getMessage() + ")");
        }
    }
}
