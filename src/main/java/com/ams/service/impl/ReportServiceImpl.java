package com.ams.service.impl;

import com.ams.dto.StudentAttendanceStats;
import com.ams.entity.Student;
import com.ams.repository.AttendanceRepository;
import com.ams.repository.StudentRepository;
import com.ams.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    public ReportServiceImpl(StudentRepository studentRepository,
                              AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    private StudentAttendanceStats calculateStats(Student student, LocalDate start, LocalDate end) {
        long presentCount = attendanceRepository.countPresentByStudentAndDateRange(
                student.getId(), start, end);
        long totalCount = attendanceRepository.countTotalByStudentAndDateRange(
                student.getId(), start, end);
        long absentCount = totalCount - presentCount;

        double percentage = 0.0;
        if (totalCount > 0) {
            percentage = ((double) presentCount / totalCount) * 100;
        }

        StudentAttendanceStats stats = new StudentAttendanceStats();
        stats.setStudentId(student.getId());
        stats.setStudentName(student.getName());
        stats.setEnrollmentNo(student.getEnrollmentNo());
        stats.setBatchCode(student.getBatch() != null ? student.getBatch().getBatchCode() : "N/A");
        stats.setTotalClasses(totalCount);
        stats.setPresentCount(presentCount);
        stats.setAbsentCount(absentCount);
        stats.setPercentage(Math.round(percentage * 100.0) / 100.0);
        stats.setBelowThreshold(percentage < 75.0);

        return stats;
    }

    @Override
    public List<StudentAttendanceStats> getStudentWiseReport(Long batchId, LocalDate start, LocalDate end) {
        List<Student> students = (batchId != null)
                ? studentRepository.findByBatchId(batchId)
                : studentRepository.findAll();
        List<StudentAttendanceStats> report = new ArrayList<>();

        for (Student student : students) {
            report.add(calculateStats(student, start, end));
        }
        return report;
    }

    @Override
    public List<StudentAttendanceStats> getPercentageReport(Long batchId, LocalDate start, LocalDate end) {
        // Same logic — belowThreshold is always calculated
        return getStudentWiseReport(batchId, start, end);
    }

    @Override
    public List<StudentAttendanceStats> getMonthlyReport(Long batchId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        return getStudentWiseReport(batchId, start, end);
    }
}
