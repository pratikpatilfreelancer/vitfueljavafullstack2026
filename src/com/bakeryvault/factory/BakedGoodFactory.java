package com.bakeryvault.factory;

import com.bakeryvault.enums.ItemType;
import com.bakeryvault.exceptions.InvalidSKUException;
import com.bakeryvault.model.BakedGood;
import com.bakeryvault.model.Bread;
import com.bakeryvault.model.Cake;
import com.bakeryvault.model.Pastry;

/**
 * Factory Design Pattern: centralizes the "which subclass do I build, and
 * how do I validate its inputs" logic in one place, so client code
 * (BakeryService, Main) never calls "new Bread(...)" directly and never
 * needs to know construction/validation details.
 */
public final class BakedGoodFactory {

    // Utility-style factory: no instances needed.
    private BakedGoodFactory() {
    }

    public static BakedGood createBread(String name, String baker, String sku, String breadType,
                                         int weightGrams, double price, ItemType dietaryType) throws InvalidSKUException {
        validateSku(sku);
        return new Bread(name, baker, sku, breadType, weightGrams, price, dietaryType);
    }

    public static BakedGood createPastry(String name, String baker, String batchNumber,
                                          String flavor, double price, ItemType dietaryType) {
        return new Pastry(name, baker, batchNumber, flavor, price, dietaryType);
    }

    public static BakedGood createCake(String name, String baker, String cakeSize,
                                        double weightKg, double price, ItemType dietaryType) {
        return new Cake(name, baker, cakeSize, weightKg, price, dietaryType);
    }

    /** Generic entry point used when the category is only known at runtime (e.g. from a menu choice). */
    public static BakedGood create(String category, ItemType dietaryType, String name, String baker,
                                    double price, String... extra) throws InvalidSKUException {
        switch (category) {
            case "BREAD":
                return createBread(name, baker, extra[0], extra[1], Integer.parseInt(extra[2]), price, dietaryType);
            case "PASTRY":
                return createPastry(name, baker, extra[0], extra[1], price, dietaryType);
            case "CAKE":
                return createCake(name, baker, extra[0], Double.parseDouble(extra[1]), price, dietaryType);
            default:
                throw new IllegalArgumentException("Unknown baked good category: " + category);
        }
    }

    private static void validateSku(String sku) throws InvalidSKUException {
        if (sku == null) {
            throw new InvalidSKUException("null");
        }
        String cleaned = sku.replace("-", "").trim().toUpperCase();
        if (cleaned.length() < 6 || cleaned.length() > 12 || !cleaned.matches("[A-Z0-9]+")) {
            throw new InvalidSKUException(sku);
        }
    }
}
