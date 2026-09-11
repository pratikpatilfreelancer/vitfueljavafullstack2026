package vanguard.service;

import vanguard.model.IncidentType;
import vanguard.model.Severity;

/** Plain result object returned by any IncidentClassifier implementation. */
public class ClassificationResult {
    private final IncidentType type;
    private final Severity severity;
    private final int confidence;
    private final String reasoning;

    public ClassificationResult(IncidentType type, Severity severity, int confidence, String reasoning) {
        this.type = type;
        this.severity = severity;
        this.confidence = confidence;
        this.reasoning = reasoning;
    }

    public IncidentType getType() { return type; }
    public Severity getSeverity() { return severity; }
    public int getConfidence() { return confidence; }
    public String getReasoning() { return reasoning; }
}
