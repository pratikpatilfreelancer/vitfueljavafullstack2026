package vanguard.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A reported emergency incident.
 * Implements Comparable so a TreeSet/sorted collection of incidents
 * naturally orders itself by severity (critical first), then by
 * report time — this is what powers the priority queue in
 * DispatchCenter without needing an external Comparator everywhere.
 */
public class Incident implements Locatable, Comparable<Incident>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String description;
    private final int x;
    private final int y;
    private final LocalDateTime createdAt;

    private IncidentType type;
    private Severity severity;
    private int confidence;
    private String reasoning;
    private IncidentStatus status;
    private String assignedResourceId;

    public Incident(String id, String description, int x, int y) {
        this.id = id;
        this.description = description;
        this.x = x;
        this.y = y;
        this.createdAt = LocalDateTime.now();
        this.status = IncidentStatus.REPORTED;
        this.severity = Severity.MEDIUM; // default until classified
        this.type = IncidentType.OTHER;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }

    @Override
    public int getX() { return x; }

    @Override
    public int getY() { return y; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public IncidentType getType() { return type; }
    public void setType(IncidentType type) { this.type = type; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public int getConfidence() { return confidence; }
    public void setConfidence(int confidence) { this.confidence = confidence; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }

    public IncidentStatus getStatus() { return status; }
    public void setStatus(IncidentStatus status) { this.status = status; }

    public String getAssignedResourceId() { return assignedResourceId; }
    public void setAssignedResourceId(String assignedResourceId) {
        this.assignedResourceId = assignedResourceId;
    }

    /**
     * Natural ordering: highest severity first; ties broken by
     * earliest report time so older incidents of equal severity
     * are handled first.
     */
    @Override
    public int compareTo(Incident other) {
        int severityCompare = Integer.compare(other.severity.getRank(), this.severity.getRank());
        if (severityCompare != 0) return severityCompare;
        return this.createdAt.compareTo(other.createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Incident)) return false;
        Incident other = (Incident) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s/%s (%d%%) @ (%d,%d) - %s",
                id, description, type, severity, confidence, x, y, status);
    }
}
