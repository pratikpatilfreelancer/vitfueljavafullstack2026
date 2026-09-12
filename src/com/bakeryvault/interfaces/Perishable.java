package com.bakeryvault.interfaces;

import java.time.LocalDate;

/**
 * Contract for baked goods that spoil and must be tracked for freshness.
 * Implemented by item types that carry a shelf life (e.g. Bread, Cake)
 * but deliberately NOT by types like Pastry, which are baked fresh each
 * morning and sold same-day only - expiration tracking would be
 * meaningless for them.
 */
public interface Perishable {
    /** The date after which this item should no longer be sold. */
    LocalDate getExpirationDate();

    boolean isExpired();

    /** Negative once the item is past its expiration date. */
    long getDaysUntilExpiration();
}
