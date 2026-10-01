package com.internships.model;

public class Student {
    private String studentId;
    private String name;
    private String email;
    private String phone;
    private String college;
    private String course;
    private Integer year;
    private String skills;
    private String resumeUrl;
    private String createdAt;

    public Student() {}

    public Student(String studentId, String name, String email, String phone, String college, String course, Integer year, String skills, String resumeUrl, String createdAt) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.college = college;
        this.course = course;
        this.year = year;
        this.skills = skills;
        this.resumeUrl = resumeUrl;
        this.createdAt = createdAt;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
