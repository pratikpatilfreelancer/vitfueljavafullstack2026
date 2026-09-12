package model;

import java.time.LocalDate;

/**
 * A job opening posted by a recruiter.
 */
public class Job {
    private int jobId;
    private int recruiterId;
    private String title;
    private String description;
    private String requiredSkills; // comma separated
    private int minExperience;
    private String location;
    private String status; // OPEN, CLOSED
    private LocalDate postedDate;

    public Job() {}

    public Job(int jobId, int recruiterId, String title, String description,
               String requiredSkills, int minExperience, String location,
               String status, LocalDate postedDate) {
        this.jobId = jobId;
        this.recruiterId = recruiterId;
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.minExperience = minExperience;
        this.location = location;
        this.status = status;
        this.postedDate = postedDate;
    }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public int getRecruiterId() { return recruiterId; }
    public void setRecruiterId(int recruiterId) { this.recruiterId = recruiterId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public int getMinExperience() { return minExperience; }
    public void setMinExperience(int minExperience) { this.minExperience = minExperience; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getPostedDate() { return postedDate; }
    public void setPostedDate(LocalDate postedDate) { this.postedDate = postedDate; }

    @Override
    public String toString() {
        return title + " (" + location + ")";
    }
}
