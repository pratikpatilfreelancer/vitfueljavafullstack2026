package com.internships.model;

public class Skill {
    private String skillId;
    private String skillName;
    private String category;
    private String createdAt;

    public Skill() {}

    public Skill(String skillId, String skillName, String category, String createdAt) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.createdAt = createdAt;
    }

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
