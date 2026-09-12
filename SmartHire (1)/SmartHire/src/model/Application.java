package model;

import java.time.LocalDate;

/**
 * A candidate's application to a job. match_score is populated by the
 * ResumeScreeningService when the application is created or re-screened.
 */
public class Application {
    private int applicationId;
    private int jobId;
    private int candidateId;
    private LocalDate appliedDate;
    private String status; // APPLIED, SHORTLISTED, REJECTED, INTERVIEW_SCHEDULED, HIRED
    private double matchScore; // 0-100

    // convenience fields populated by joined DAO queries (not persisted directly)
    private String jobTitle;
    private String candidateName;

    public Application() {}

    public Application(int applicationId, int jobId, int candidateId,
                        LocalDate appliedDate, String status, double matchScore) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.candidateId = candidateId;
        this.appliedDate = appliedDate;
        this.status = status;
        this.matchScore = matchScore;
    }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public int getCandidateId() { return candidateId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }

    public LocalDate getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
}
