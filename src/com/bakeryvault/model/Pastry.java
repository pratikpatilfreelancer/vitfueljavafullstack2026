package com.bakeryvault.model;

import com.bakeryvault.enums.ItemType;

/**
 * A daily pastry item. IS-A BakedGood, but deliberately does NOT
 * implement Perishable or PreOrderable - pastries are baked fresh each
 * morning and sold same-day only. This is a real design decision, not
 * an oversight: it shows that interfaces should only be implemented
 * where the behavior genuinely applies.
 */
public class Pastry extends BakedGood {

    private static final long serialVersionUID = 1L;

    private String batchNumber;
    private String flavor;

    public Pastry(String name, String baker, String batchNumber, String flavor,
                  double price, ItemType dietaryType) {
        super(name, baker, price, dietaryType);
        this.batchNumber = batchNumber;
        this.flavor = flavor;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public String getFlavor() {
        return flavor;
    }

    @Override
    public String getItemType() {
        return "PASTRY";
    }

    @Override
    public String displayInfo() {
        return String.format("[PASTRY] %s | Batch #%s | Flavor: %s | Price: %.2f | %s",
                getName(), batchNumber, flavor, getPrice(),
                isAvailable() ? "In stock" : "Sold out");
    }
}
