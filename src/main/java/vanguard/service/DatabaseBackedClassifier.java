package vanguard.service;

import vanguard.db.PatternDAO;

import java.sql.SQLException;
import java.util.*;


public class DatabaseBackedClassifier implements IncidentClassifier {

    private static final double MATCH_THRESHOLD = 0.15;

    private final PatternDAO patternDAO = new PatternDAO();
    private final SqlKeywordClassifier keywordClassifier = new SqlKeywordClassifier();
    private final RuleBasedClassifier lastResortFallback = new RuleBasedClassifier();
    private final List<StoredPattern> patterns = new ArrayList<>();

    public DatabaseBackedClassifier() {
        loadPatterns();
    }

    private void loadPatterns() {
        try {
            patterns.addAll(patternDAO.findAll());
            System.out.println("DatabaseBackedClassifier: loaded "
                    + patterns.size() + " reference patterns from MySQL.");
        } catch (SQLException e) {
            System.out.println("DatabaseBackedClassifier: MySQL not reachable ("
                    + e.getMessage() + "). Will rely on keyword-based matching instead.");
        }
    }

    @Override
    public ClassificationResult classify(String description) {
        StoredPattern best = null;
        double bestScore = 0.0;

        for (StoredPattern pattern : patterns) {
            double score = similarity(description, pattern.getDescription());
            if (score > bestScore) {
                bestScore = score;
                best = pattern;
            }
        }

        if (best != null && bestScore >= MATCH_THRESHOLD) {
            int confidence = (int) Math.round(Math.min(bestScore, 1.0) * 100);
            String reasoning = String.format(
                    "Matched a similar past incident (%.0f%% similar): \"%s\"",
                    bestScore * 100, truncate(best.getDescription(), 60));
            return new ClassificationResult(best.getType(), best.getSeverity(), confidence, reasoning);
        }

        // No close full-description match — score against the SQL
        // keyword dataset instead, with a hardcoded regex fallback
        // only if something is wrong with both database paths.
        try {
            return keywordClassifier.classify(description);
        } catch (Exception e) {
            return lastResortFallback.classify(description);
        }
    }

    /**
     * Jaccard similarity between the two descriptions' word sets:
     * (shared words) / (all distinct words across both). Simple,
     * explainable, and needs nothing beyond core String/Collections —
     * appropriate for a Core Java project rather than pulling in an
     * NLP library.
     */
    private double similarity(String a, String b) {
        Set<String> wordsA = tokenize(a);
        Set<String> wordsB = tokenize(b);
        if (wordsA.isEmpty() || wordsB.isEmpty()) return 0.0;

        Set<String> intersection = new HashSet<>(wordsA);
        intersection.retainAll(wordsB);

        Set<String> union = new HashSet<>(wordsA);
        union.addAll(wordsB);

        return (double) intersection.size() / union.size();
    }

    private Set<String> tokenize(String text) {
        String[] words = text.toLowerCase().split("[^a-z]+");
        Set<String> tokens = new HashSet<>();
        for (String w : words) {
            if (w.length() > 2) tokens.add(w); // skip tiny/stop-ish words
        }
        return tokens;
    }

    private String truncate(String text, int maxLen) {
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }
}
