package com.bakeryvault.interfaces;

/**
 * Contract for items that a customer can pre-order while the current
 * batch is sold out. Demonstrates multiple inheritance of type via
 * interfaces when combined with Perishable.
 */
public interface PreOrderable {
    void preOrder(String customerId);
    String getPreOrderedBy();
    boolean isPreOrdered();
}
