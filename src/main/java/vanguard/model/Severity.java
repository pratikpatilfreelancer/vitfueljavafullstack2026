package vanguard.model;

/**
 * Severity levels for an incident.
 * Enums are implicitly final classes in Java (ties to the "final class"
 * concept) and give us type-safety we would not get from raw ints or
 * Strings when representing a fixed set of values.
 */
public enum Severity {
    LOW(0), MEDIUM(1), HIGH(2), CRITICAL(3);

    private final int rank;

    Severity(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }
}
