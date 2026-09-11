package vanguard.model;

/**
 * Anything that has a position on the grid implements this.
 * Both Resource and Incident implement it, which is what lets
 * GeoUtils.distance() work on either type interchangeably —
 * a simple demonstration of programming to an interface.
 */
public interface Locatable {
    int getX();
    int getY();
}
