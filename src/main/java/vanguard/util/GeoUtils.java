package vanguard.util;

import vanguard.model.Locatable;

/**
 * Pure static utility class (never instantiated) — demonstrates the
 * static keyword applied at both the method level here and is used
 * throughout the project via static import-style calls (GeoUtils.distance).
 */
public final class GeoUtils {

    // Private constructor: prevents instantiation of a utility class.
    private GeoUtils() { }

    public static double distance(Locatable a, Locatable b) {
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        return Math.sqrt((double) (dx * dx) + (double) (dy * dy));
    }
}
