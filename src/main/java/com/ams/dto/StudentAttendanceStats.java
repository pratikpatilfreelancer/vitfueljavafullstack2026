package com.ams.dto;

/**
 * DTO holding attendance statistics for a single student, used in report views.
 */
public class StudentAttendanceStats {

    private Long studentId;
    private String studentName;
    private String enrollmentNo;
    private String batchCode;
    private long totalClasses;
    private long presentCount;
    private long absentCount;
    private double percentage;
    private boolean belowThreshold;

    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getEnrollmentNo() { return enrollmentNo; }
    public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }

    public String getBatchCode() { return batchCode; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }

    public long getTotalClasses() { return totalClasses; }
    public void setTotalClasses(long totalClasses) { this.totalClasses = totalClasses; }

    public long getPresentCount() { return presentCount; }
    public void setPresentCount(long presentCount) { this.presentCount = presentCount; }

    public long getAbsentCount() { return absentCount; }
    public void setAbsentCount(long absentCount) { this.absentCount = absentCount; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public boolean isBelowThreshold() { return belowThreshold; }
    public void setBelowThreshold(boolean belowThreshold) { this.belowThreshold = belowThreshold; }
}
