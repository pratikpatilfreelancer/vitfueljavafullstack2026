package com.internships.model;

public class InternshipSkill {
    private String internshipId;
    private String skillId;
    private String createdAt;

    public InternshipSkill() {}

    public InternshipSkill(String internshipId, String skillId, String createdAt) {
        this.internshipId = internshipId;
        this.skillId = skillId;
        this.createdAt = createdAt;
    }

    public String getInternshipId() { return internshipId; }
    public void setInternshipId(String internshipId) { this.internshipId = internshipId; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
