package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * An interview scheduled against a specific application.
 */
public class Interview {
    private int interviewId;
    private int applicationId;
    private LocalDate interviewDate;
    private LocalTime interviewTime;
    private String mode;    // ONLINE, IN_PERSON, PHONE
    private String status;  // SCHEDULED, COMPLETED, CANCELLED
    private String feedback;
    private int rating;     // 1-5, 0 if not yet rated

    // convenience fields populated by joined DAO queries
    private String candidateName;
    private String jobTitle;

    public Interview() {}

    public Interview(int interviewId, int applicationId, LocalDate interviewDate,
                      LocalTime interviewTime, String mode, String status,
                      String feedback, int rating) {
        this.interviewId = interviewId;
        this.applicationId = applicationId;
        this.interviewDate = interviewDate;
        this.interviewTime = interviewTime;
        this.mode = mode;
        this.status = status;
        this.feedback = feedback;
        this.rating = rating;
    }

    public int getInterviewId() { return interviewId; }
    public void setInterviewId(int interviewId) { this.interviewId = interviewId; }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public LocalDate getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDate interviewDate) { this.interviewDate = interviewDate; }

    public LocalTime getInterviewTime() { return interviewTime; }
    public void setInterviewTime(LocalTime interviewTime) { this.interviewTime = interviewTime; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
}
