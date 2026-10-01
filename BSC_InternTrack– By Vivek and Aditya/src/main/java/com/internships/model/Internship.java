package com.internships.model;

public class Internship {
    private String internshipId;
    private String companyId;
    private String title;
    private String description;
    private String location;
    private String duration;
    private Double stipend;
    private String requiredSkills;
    private String applicationDeadline;
    private String createdAt;

    public Internship() {}

    public Internship(String internshipId, String companyId, String title, String description, String location, String duration, Double stipend, String requiredSkills, String applicationDeadline, String createdAt) {
        this.internshipId = internshipId;
        this.companyId = companyId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.duration = duration;
        this.stipend = stipend;
        this.requiredSkills = requiredSkills;
        this.applicationDeadline = applicationDeadline;
        this.createdAt = createdAt;
    }

    public String getInternshipId() { return internshipId; }
    public void setInternshipId(String internshipId) { this.internshipId = internshipId; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public Double getStipend() { return stipend; }
    public void setStipend(Double stipend) { this.stipend = stipend; }
    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }
    public String getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(String applicationDeadline) { this.applicationDeadline = applicationDeadline; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
