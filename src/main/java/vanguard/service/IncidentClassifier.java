package vanguard.service;


@FunctionalInterface
public interface IncidentClassifier {
    ClassificationResult classify(String description);
}
