package com.internships.model;

public class StudentSkill {
    private String studentId;
    private String skillId;
    private String createdAt;

    public StudentSkill() {}

    public StudentSkill(String studentId, String skillId, String createdAt) {
        this.studentId = studentId;
        this.skillId = skillId;
        this.createdAt = createdAt;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
