package com.bakeryvault.model;

import com.bakeryvault.enums.ItemType;
import com.bakeryvault.interfaces.Perishable;
import com.bakeryvault.interfaces.PreOrderable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * A loaf of bread. IS-A BakedGood. Tracks its own freshness window and
 * supports pre-ordering, demonstrating multiple inheritance of type
 * through interfaces.
 */
public class Bread extends BakedGood implements Perishable, PreOrderable {

    private static final long serialVersionUID = 1L;
    private static final int SHELF_LIFE_DAYS = 3;

    private final String sku;
    private String breadType;
    private int weightGrams;

    private String preOrderedByCustomerId;

    public Bread(String name, String baker, String sku, String breadType,
                 int weightGrams, double price, ItemType dietaryType) {
        super(name, baker, price, dietaryType);
        this.sku = sku;
        this.breadType = breadType;
        this.weightGrams = weightGrams;
        this.preOrderedByCustomerId = null;
    }

    public String getSku() {
        return sku;
    }

    public String getBreadType() {
        return breadType;
    }

    public void setBreadType(String breadType) {
        this.breadType = breadType;
    }

    public int getWeightGrams() {
        return weightGrams;
    }

    @Override
    public LocalDate getExpirationDate() {
        return getBakeDate().plusDays(SHELF_LIFE_DAYS);
    }

    @Override
    public boolean isExpired() {
        return LocalDate.now().isAfter(getExpirationDate());
    }

    @Override
    public long getDaysUntilExpiration() {
        return ChronoUnit.DAYS.between(LocalDate.now(), getExpirationDate());
    }

    @Override
    public void preOrder(String customerId) {
        this.preOrderedByCustomerId = customerId;
    }

    @Override
    public String getPreOrderedBy() {
        return preOrderedByCustomerId;
    }

    @Override
    public boolean isPreOrdered() {
        return preOrderedByCustomerId != null;
    }

    @Override
    public String getItemType() {
        return "BREAD";
    }

    @Override
    public String displayInfo() {
        return String.format("[BREAD] %s by %s | SKU: %s | Type: %s | Weight: %dg | Price: %.2f | %s",
                getName(), getBaker(), sku, breadType, weightGrams, getPrice(),
                isAvailable() ? "In stock" : "Sold out");
    }
}
