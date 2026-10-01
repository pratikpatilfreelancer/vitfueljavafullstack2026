package com.internships.model;

public class Company {
    private String companyId;
    private String companyName;
    private String location;
    private String industry;
    private String website;
    private String contactEmail;
    private String createdAt;

    public Company() {}

    public Company(String companyId, String companyName, String location, String industry, String website, String contactEmail, String createdAt) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.location = location;
        this.industry = industry;
        this.website = website;
        this.contactEmail = contactEmail;
        this.createdAt = createdAt;
    }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
