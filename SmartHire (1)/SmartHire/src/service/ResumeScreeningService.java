package service;

import model.Candidate;
import model.Job;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Very small, transparent "resume screening" algorithm.
 *
 * SmartHire does not use any external AI/NLP library — instead it computes a
 * match score out of 100 from two simple, explainable signals:
 *
 *   1. Skill overlap (70% weight): what fraction of the job's required
 *      skills also appear in the candidate's skill list / resume text.
 *   2. Experience fit (30% weight): how the candidate's years of experience
 *      compare to the job's minimum requirement.
 *
 * This keeps the project honest about what it does (keyword/rule based
 * screening) rather than pretending to be a full ML resume parser, while
 * still producing a genuinely useful ranking for recruiters.
 */
public class ResumeScreeningService {

    private static final double SKILL_WEIGHT = 0.70;
    private static final double EXPERIENCE_WEIGHT = 0.30;

    /**
     * Computes a 0-100 match score between a candidate and a job.
     */
    public double calculateMatchScore(Candidate candidate, Job job) {
        double skillScore = calculateSkillMatch(candidate, job);
        double experienceScore = calculateExperienceMatch(candidate, job);
        double total = (skillScore * SKILL_WEIGHT) + (experienceScore * EXPERIENCE_WEIGHT);
        return Math.round(total * 100.0) / 100.0;
    }

    /** Percentage (0-100) of the job's required skills found in the candidate's profile. */
    public double calculateSkillMatch(Candidate candidate, Job job) {
        Set<String> requiredSkills = tokenizeSkills(job.getRequiredSkills());
        if (requiredSkills.isEmpty()) {
            return 100.0; // job listed no specific skills, don't penalise anyone
        }

        String candidateProfile = ((candidate.getSkills() == null ? "" : candidate.getSkills()) + " "
                + (candidate.getResumeText() == null ? "" : candidate.getResumeText())).toLowerCase();

        int matched = 0;
        for (String skill : requiredSkills) {
            if (candidateProfile.contains(skill)) {
                matched++;
            }
        }
        return (matched * 100.0) / requiredSkills.size();
    }

    /** Score based on how candidate experience compares to the job's minimum. */
    public double calculateExperienceMatch(Candidate candidate, Job job) {
        int required = job.getMinExperience();
        int actual = candidate.getExperienceYears();

        if (required <= 0) {
            return 100.0;
        }
        if (actual >= required) {
            // meets or exceeds requirement -> full score, capped so wildly
            // over-qualified candidates don't just get more and more score
            return 100.0;
        }
        // partial credit proportional to how close they are
        double ratio = (double) actual / (double) required;
        return Math.max(0.0, ratio * 100.0);
    }

    /** Splits a comma/semicolon separated skill string into a normalised, lower-case set. */
    private Set<String> tokenizeSkills(String rawSkills) {
        Set<String> set = new HashSet<>();
        if (rawSkills == null || rawSkills.trim().isEmpty()) {
            return set;
        }
        String[] parts = rawSkills.split("[,;/]");
        for (String part : Arrays.asList(parts)) {
            String cleaned = part.trim().toLowerCase();
            if (!cleaned.isEmpty()) {
                set.add(cleaned);
            }
        }
        return set;
    }

    /** Human readable label used in the UI next to a numeric score. */
    public String scoreLabel(double score) {
        if (score >= 80) return "Strong Match";
        if (score >= 60) return "Good Match";
        if (score >= 40) return "Moderate Match";
        return "Weak Match";
    }
}
