package com.examapp.examapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidateName;
    private int score;
    private LocalDateTime submittedOn;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Exam exam;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public LocalDateTime getSubmittedOn() { return submittedOn; }
    public void setSubmittedOn(LocalDateTime submittedOn) { this.submittedOn = submittedOn; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }
}