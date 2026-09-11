package vanguard.service;

import vanguard.db.KeywordDAO;
import vanguard.exceptions.InvalidReportException;
import vanguard.model.IncidentType;
import vanguard.model.Severity;

import java.sql.SQLException;
import java.util.*;

/**
 * Classifies incidents by matching words in the description against
 * a dataset of keywords stored in the keyword_rules SQL table — e.g.
 * "fire" -> FIRE type, weight 25; "trapped" -> STRUCTURAL, weight 30.
 * Each matched keyword votes for an incident type and adds to a
 * running severity score. This is the point of storing the dataset in
 * SQL rather than hardcoding it: new keywords, typo variants, or
 * re-tuned weights are just INSERT/UPDATE statements — no Java code
 * changes or recompiling needed.
 *
 * If MySQL isn't reachable, falls back to a small built-in seed list
 * (mirroring the same words in schema.sql) so the app still works
 * standalone.
 */
public class SqlKeywordClassifier implements IncidentClassifier {

    private final List<KeywordRule> rules = new ArrayList<>();
    private final Random random = new Random();

    public SqlKeywordClassifier() {
        loadRules();
    }

    private void loadRules() {
        try {
            rules.addAll(new KeywordDAO().findAll());
            System.out.println("SqlKeywordClassifier: loaded " + rules.size()
                    + " keyword rules from MySQL.");
        } catch (SQLException e) {
            System.out.println("SqlKeywordClassifier: MySQL not reachable ("
                    + e.getMessage() + "). Using built-in keyword list.");
            rules.addAll(builtInFallbackRules());
        }
    }

    @Override
    public ClassificationResult classify(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidReportException("Incident description cannot be empty.");
        }

        Set<String> words = tokenize(description);
        Map<IncidentType, Integer> typeScores = new EnumMap<>(IncidentType.class);
        List<String> matchedKeywords = new ArrayList<>();
        int severityScore = 40; // same baseline as RuleBasedClassifier, for comparable results

        for (KeywordRule rule : rules) {
            if (!words.contains(rule.getKeyword().toLowerCase())) continue;

            matchedKeywords.add(rule.getKeyword() + "(" + (rule.getSeverityWeight() >= 0 ? "+" : "")
                    + rule.getSeverityWeight() + ")");
            severityScore += rule.getSeverityWeight();

            if (rule.getType() != null) {
                typeScores.merge(rule.getType(), rule.getSeverityWeight(), Integer::sum);
            }
        }

        IncidentType bestType = typeScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(IncidentType.OTHER);

        severityScore = Math.max(5, Math.min(97, severityScore + random.nextInt(10)));
        Severity severity;
        if (severityScore >= 75) severity = Severity.CRITICAL;
        else if (severityScore >= 55) severity = Severity.HIGH;
        else if (severityScore >= 30) severity = Severity.MEDIUM;
        else severity = Severity.LOW;

        String reasoning = matchedKeywords.isEmpty()
                ? "No keywords from the SQL dataset matched; defaulted to OTHER/baseline severity."
                : "SQL keyword matches: " + String.join(", ", matchedKeywords);

        return new ClassificationResult(bestType, severity, severityScore, reasoning);
    }

    private Set<String> tokenize(String text) {
        String[] words = text.toLowerCase().split("[^a-z]+");
        return new HashSet<>(Arrays.asList(words));
    }

    /** Mirrors sql/schema.sql's keyword_rules seed data, for offline use. */
    private List<KeywordRule> builtInFallbackRules() {
        List<KeywordRule> list = new ArrayList<>();
        list.add(new KeywordRule("fire", IncidentType.FIRE, 25));
        list.add(new KeywordRule("smoke", IncidentType.FIRE, 15));
        list.add(new KeywordRule("burn", IncidentType.FIRE, 20));
        list.add(new KeywordRule("burning", IncidentType.FIRE, 20));
        list.add(new KeywordRule("blaze", IncidentType.FIRE, 20));
        list.add(new KeywordRule("wildfire", IncidentType.FIRE, 25));
        list.add(new KeywordRule("flood", IncidentType.FLOOD, 20));
        list.add(new KeywordRule("flooding", IncidentType.FLOOD, 20));
        list.add(new KeywordRule("water", IncidentType.FLOOD, 10));
        list.add(new KeywordRule("drown", IncidentType.FLOOD, 25));
        list.add(new KeywordRule("drowning", IncidentType.FLOOD, 25));
        list.add(new KeywordRule("stranded", IncidentType.FLOOD, 15));
        list.add(new KeywordRule("submerged", IncidentType.FLOOD, 20));
        list.add(new KeywordRule("collapse", IncidentType.STRUCTURAL, 30));
        list.add(new KeywordRule("collapsed", IncidentType.STRUCTURAL, 30));
        list.add(new KeywordRule("trapped", IncidentType.STRUCTURAL, 30));
        list.add(new KeywordRule("debris", IncidentType.STRUCTURAL, 20));
        list.add(new KeywordRule("rubble", IncidentType.STRUCTURAL, 20));
        list.add(new KeywordRule("landslide", IncidentType.STRUCTURAL, 25));
        list.add(new KeywordRule("scaffolding", IncidentType.STRUCTURAL, 15));
        list.add(new KeywordRule("injured", IncidentType.MEDICAL, 20));
        list.add(new KeywordRule("injury", IncidentType.MEDICAL, 15));
        list.add(new KeywordRule("unconscious", IncidentType.MEDICAL, 30));
        list.add(new KeywordRule("bleeding", IncidentType.MEDICAL, 25));
        list.add(new KeywordRule("chest", IncidentType.MEDICAL, 20));
        list.add(new KeywordRule("pain", IncidentType.MEDICAL, 15));
        list.add(new KeywordRule("breathing", IncidentType.MEDICAL, 20));
        list.add(new KeywordRule("allergic", IncidentType.MEDICAL, 15));
        list.add(new KeywordRule("multiple", null, 20));
        list.add(new KeywordRule("several", null, 15));
        list.add(new KeywordRule("many", null, 15));
        list.add(new KeywordRule("critical", null, 25));
        list.add(new KeywordRule("dying", null, 30));
        list.add(new KeywordRule("spreading", null, 20));
        list.add(new KeywordRule("minor", null, -20));
        list.add(new KeywordRule("small", null, -10));
        list.add(new KeywordRule("contained", null, -15));
        return list;
    }
}
