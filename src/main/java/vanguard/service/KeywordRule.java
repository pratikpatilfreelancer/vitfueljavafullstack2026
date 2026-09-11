package vanguard.service;

import vanguard.model.IncidentType;


public class KeywordRule {
    private final String keyword;
    private final IncidentType type; // nullable
    private final int severityWeight;

    public KeywordRule(String keyword, IncidentType type, int severityWeight) {
        this.keyword = keyword;
        this.type = type;
        this.severityWeight = severityWeight;
    }

    public String getKeyword() { return keyword; }
    public IncidentType getType() { return type; }
    public int getSeverityWeight() { return severityWeight; }
}
