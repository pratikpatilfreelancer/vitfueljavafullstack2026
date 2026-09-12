package com.bakeryvault.enums;

/**
 * Represents the dietary classification of a baked good. Assigned to
 * every BakedGood at creation time so the inventory can be filtered by
 * dietary need (e.g. gluten-free, vegan) regardless of which concrete
 * subclass (Bread, Cake, Pastry) the item happens to be. Used by
 * BakedGoodFactory when constructing new items.
 */
public enum ItemType {
    STANDARD,
    VEGAN,
    GLUTEN_FREE
}
