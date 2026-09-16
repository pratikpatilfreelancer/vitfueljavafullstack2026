package com.ams.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "batch")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Batch code is required")
    @Size(max = 20)
    @Column(name = "batch_code", nullable = false, unique = true, length = 20)
    private String batchCode;

    @NotBlank(message = "Subject is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @Size(max = 20)
    @Column(name = "batch_days", length = 20)
    private String batchDays;

    @Size(max = 20)
    @Column(name = "batch_timing", length = 20)
    private String batchTiming;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL)
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL)
    private List<Attendance> attendanceRecords = new ArrayList<>();

    // Constructors
    public Batch() {}

    public Batch(String batchCode, String subject, User teacher, String batchDays, String batchTiming) {
        this.batchCode = batchCode;
        this.subject = subject;
        this.teacher = teacher;
        this.batchDays = batchDays;
        this.batchTiming = batchTiming;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBatchCode() { return batchCode; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public User getTeacher() { return teacher; }
    public void setTeacher(User teacher) { this.teacher = teacher; }

    public String getBatchDays() { return batchDays; }
    public void setBatchDays(String batchDays) { this.batchDays = batchDays; }

    public String getBatchTiming() { return batchTiming; }
    public void setBatchTiming(String batchTiming) { this.batchTiming = batchTiming; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public List<Attendance> getAttendanceRecords() { return attendanceRecords; }
    public void setAttendanceRecords(List<Attendance> attendanceRecords) { this.attendanceRecords = attendanceRecords; }
}
