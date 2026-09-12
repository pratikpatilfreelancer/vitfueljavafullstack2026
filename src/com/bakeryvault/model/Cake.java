package com.bakeryvault.model;

import com.bakeryvault.enums.ItemType;
import com.bakeryvault.interfaces.Perishable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * A made-to-order custom cake. IS-A BakedGood. Always in stock (baked
 * fresh per order, so there's no scarcity to run out of) - a genuine
 * behavioral override of the base class's availability semantics, and
 * a good example of polymorphism beyond just changing displayInfo().
 */
public class Cake extends BakedGood implements Perishable {

    private static final long serialVersionUID = 1L;
    private static final int SHELF_LIFE_DAYS = 5;

    private String cakeSize;
    private double weightKg;

    public Cake(String name, String baker, String cakeSize, double weightKg,
                double price, ItemType dietaryType) {
        super(name, baker, price, dietaryType);
        this.cakeSize = cakeSize;
        this.weightKg = weightKg;
        setAvailable(true);
    }

    public String getCakeSize() {
        return cakeSize;
    }

    public void setCakeSize(String cakeSize) {
        this.cakeSize = cakeSize;
    }

    public double getWeightKg() {
        return weightKg;
    }

    @Override
    public void setAvailable(boolean available) {
        // Cakes are baked to order with no scarcity - always available.
        super.setAvailable(true);
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
    public String getItemType() {
        return "CAKE";
    }

    @Override
    public String displayInfo() {
        return String.format("[CAKE] %s by %s | Size: %s | Weight: %.1fkg | Price: %.2f | Always available (made to order)",
                getName(), getBaker(), cakeSize, weightKg, getPrice());
    }
}
