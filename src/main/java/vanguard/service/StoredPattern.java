package vanguard.service;

import vanguard.model.IncidentType;
import vanguard.model.Severity;

/**
 * One entry in the "case base" DatabaseBackedClassifier compares new
 * incident reports against — a past report paired with its confirmed
 * correct type/severity.
 */
public class StoredPattern {
    private final String description;
    private final IncidentType type;
    private final Severity severity;

    public StoredPattern(String description, IncidentType type, Severity severity) {
        this.description = description;
        this.type = type;
        this.severity = severity;
    }

    public String getDescription() { return description; }
    public IncidentType getType() { return type; }
    public Severity getSeverity() { return severity; }
}
