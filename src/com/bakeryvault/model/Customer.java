package com.bakeryvault.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A registered bakery customer. HAS-A relationship: a Customer HAS-A
 * list of purchased item ids (composition, not inheritance) - contrasted
 * with the IS-A relationships in the BakedGood hierarchy.
 */
public class Customer implements Serializable, Comparable<Customer> {

    private static final long serialVersionUID = 1L;
    public static final int MAX_PURCHASE_LIMIT = 3;

    private static int idSequence = 5000;

    private final String customerId;
    private String name;
    private String email;
    private final List<String> purchasedItemIds;

    public Customer(String name, String email) {
        this.customerId = "CUST-" + nextId();
        this.name = name;
        this.email = email;
        this.purchasedItemIds = new ArrayList<>();
    }

    private static synchronized int nextId() {
        return ++idSequence;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getPurchasedItemIds() {
        return Collections.unmodifiableList(purchasedItemIds);
    }

    public boolean isAtPurchaseLimit() {
        return purchasedItemIds.size() >= MAX_PURCHASE_LIMIT;
    }

    public void addPurchasedItem(String itemId) {
        purchasedItemIds.add(itemId);
    }

    public void removePurchasedItem(String itemId) {
        purchasedItemIds.remove(itemId);
    }

    @Override
    public String toString() {
        return String.format("[CUSTOMER] %s (%s) | %s | Purchased: %d/%d",
                name, customerId, email, purchasedItemIds.size(), MAX_PURCHASE_LIMIT);
    }

    /**
     * Natural ordering for Customer IS by name - unlike BakedGood, a
     * customer list has one obvious default sort, so Comparable is the
     * right tool here (Comparator is reserved for the alternate/
     * contextual orderings in ItemComparators).
     */
    @Override
    public int compareTo(Customer other) {
        return this.name.compareToIgnoreCase(other.name);
    }
}
