package com.bakeryvault.utils;

import com.bakeryvault.model.BakedGood;
import com.bakeryvault.model.Bread;

import java.util.Comparator;

/**
 * Central place for reusable Comparators, kept separate from BakedGood
 * itself. BakedGood does NOT implement Comparable because there is no
 * single "natural" ordering for a mixed inventory of bread/pastries/
 * cakes - ordering is always context-dependent, which is exactly when
 * you should prefer Comparator over Comparable.
 */
public final class ItemComparators {

    private ItemComparators() {
    }

    public static final Comparator<BakedGood> BY_NAME =
            Comparator.comparing(BakedGood::getName, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<BakedGood> BY_BAKER =
            Comparator.comparing(BakedGood::getBaker, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<BakedGood> BY_CATEGORY_THEN_NAME =
            Comparator.comparing(BakedGood::getItemType).thenComparing(BY_NAME);

    /** Sorts cheapest-first - meaningful across the whole mixed inventory. */
    public static final Comparator<BakedGood> BY_PRICE =
            Comparator.comparingDouble(BakedGood::getPrice);

    /** Sorts freshest (most recently baked) first. */
    public static final Comparator<BakedGood> BY_BAKE_DATE =
            Comparator.comparing(BakedGood::getBakeDate).reversed();

    /** Bread specifically can be compared by weight - only meaningful within that subtype. */
    public static final Comparator<Bread> BY_WEIGHT =
            Comparator.comparingInt(Bread::getWeightGrams);
}
