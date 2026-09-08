package com.bakeryvault.model;

import com.bakeryvault.enums.ItemType;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Abstract base class representing any item the bakery can stock.
 * Cannot be instantiated directly - concrete behavior is supplied by
 * subclasses such as Bread, Cake, and Pastry (IS-A relationships).
 *
 * Demonstrates: abstraction, encapsulation, constructors, this/super,
 * static id generation, and Serializable for file persistence.
 */
public abstract class BakedGood implements Serializable {

    private static final long serialVersionUID = 1L;

    // static counter shared across ALL subclasses - one sequence for every item.
    // Access is synchronized because item creation can happen from multiple
    // threads (see the multithreaded purchase demo in Main).
    private static int idSequence = 1000;

    private final String itemId;
    private String name;
    private String baker;
    private boolean available;
    private double price;
    private final LocalDate bakeDate;
    private ItemType dietaryType;

    protected BakedGood(String name, String baker, double price, ItemType dietaryType) {
        this.itemId = "BG-" + nextId();
        this.name = name;
        this.baker = baker;
        this.available = true;
        this.price = price;
        this.bakeDate = LocalDate.now();
        this.dietaryType = dietaryType;
    }

    private static synchronized int nextId() {
        return ++idSequence;
    }

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaker() {
        return baker;
    }

    public void setBaker(String baker) {
        this.baker = baker;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getBakeDate() {
        return bakeDate;
    }

    public ItemType getDietaryType() {
        return dietaryType;
    }

    public void setDietaryType(ItemType dietaryType) {
        this.dietaryType = dietaryType;
    }

    /**
     * Every concrete item type must describe itself. Subclasses override
     * this to include type-specific fields - a genuine behavioral
     * difference, not just a relabeled toString().
     */
    public abstract String displayInfo();

    /**
     * Each item type reports what "kind" it is - used by search/filter
     * logic and by the Factory when reconstructing items. Not to be
     * confused with ItemType (the dietary tag) - this is the class
     * discriminator: BREAD, CAKE, or PASTRY.
     */
    public abstract String getItemType();

    @Override
    public String toString() {
        return displayInfo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BakedGood)) return false;
        BakedGood other = (BakedGood) o;
        return itemId.equals(other.itemId);
    }

    @Override
    public int hashCode() {
        return itemId.hashCode();
    }
}
