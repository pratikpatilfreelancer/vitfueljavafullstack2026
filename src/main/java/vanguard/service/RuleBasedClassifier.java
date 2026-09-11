package vanguard.service;

import vanguard.model.IncidentType;
import vanguard.model.Severity;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * Rule-based (keyword matching) implementation of IncidentClassifier.
 * This plays the same role the cloud/AI version of this project gave
 * to an LLM call — here it's done with plain Java String/regex logic,
 * which is a fair substitute for a Core Java course project and keeps
 * the whole system runnable with zero external dependencies or API keys.
 */
public class RuleBasedClassifier implements IncidentClassifier {

    private static final Pattern FIRE_PATTERN =
            Pattern.compile("fire|smoke|burn|blaze", Pattern.CASE_INSENSITIVE);
    private static final Pattern FLOOD_PATTERN =
            Pattern.compile("flood|water|drown|stranded|submerg", Pattern.CASE_INSENSITIVE);
    private static final Pattern STRUCTURAL_PATTERN =
            Pattern.compile("collapse|trapped|debris|rubble|structur", Pattern.CASE_INSENSITIVE);
    private static final Pattern MEDICAL_PATTERN =
            Pattern.compile("injur|unconscious|pain|medical|bleeding|chest", Pattern.CASE_INSENSITIVE);
    private static final Pattern SEVERE_PATTERN =
            Pattern.compile("multiple|several|many|spreading|critical|dying", Pattern.CASE_INSENSITIVE);
    private static final Pattern MINOR_PATTERN =
            Pattern.compile("minor|small|contained|no injur", Pattern.CASE_INSENSITIVE);

    private final Random random = new Random();

    @Override
    public ClassificationResult classify(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new vanguard.exceptions.InvalidReportException(
                    "Incident description cannot be empty.");
        }

        IncidentType type = IncidentType.OTHER;
        int score = 40;

        if (FIRE_PATTERN.matcher(description).find()) {
            type = IncidentType.FIRE;
            score += 25;
        } else if (FLOOD_PATTERN.matcher(description).find()) {
            type = IncidentType.FLOOD;
            score += 20;
        } else if (STRUCTURAL_PATTERN.matcher(description).find()) {
            type = IncidentType.STRUCTURAL;
            score += 30;
        } else if (MEDICAL_PATTERN.matcher(description).find()) {
            type = IncidentType.MEDICAL;
            score += 25;
        }

        if (SEVERE_PATTERN.matcher(description).find()) score += 25;
        if (MINOR_PATTERN.matcher(description).find()) score -= 25;

        score += random.nextInt(10);
        score = Math.max(5, Math.min(97, score));

        Severity severity;
        if (score >= 75) severity = Severity.CRITICAL;
        else if (score >= 55) severity = Severity.HIGH;
        else if (score >= 30) severity = Severity.MEDIUM;
        else severity = Severity.LOW;

        String reasoning = "Keyword-based rule match on description text.";
        return new ClassificationResult(type, severity, score, reasoning);
    }
}
