package com.ams.service.impl;

import com.ams.entity.Attendance;
import com.ams.entity.Batch;
import com.ams.entity.Student;
import com.ams.entity.User;
import com.ams.exception.FutureDateException;
import com.ams.exception.ResourceNotFoundException;
import com.ams.repository.AttendanceRepository;
import com.ams.repository.BatchRepository;
import com.ams.repository.StudentRepository;
import com.ams.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final BatchRepository batchRepository;
    private final StudentRepository studentRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                  BatchRepository batchRepository,
                                  StudentRepository studentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.batchRepository = batchRepository;
        this.studentRepository = studentRepository;
    }

    private void validateDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new FutureDateException("Cannot mark attendance for a future date: " + date);
        }
    }

    @Override
    @Transactional
    public void markBatchAttendance(Long batchId, LocalDate date, List<Long> presentStudentIds, User markedBy) {
        validateDate(date);
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + batchId));

        List<Student> allStudents = studentRepository.findByBatchId(batchId);
        List<Attendance> records = new ArrayList<>();

        for (Student student : allStudents) {
            Attendance attendance = attendanceRepository
                    .findByStudentIdAndAttendanceDate(student.getId(), date)
                    .orElseGet(Attendance::new);

            attendance.setStudent(student);
            attendance.setBatch(batch);
            attendance.setAttendanceDate(date);
            attendance.setMarkedTime(LocalTime.now());
            attendance.setMarkedBy(markedBy);

            if (presentStudentIds != null && presentStudentIds.contains(student.getId())) {
                attendance.setStatus(Attendance.Status.PRESENT);
            } else {
                attendance.setStatus(Attendance.Status.ABSENT);
            }
            records.add(attendance);
        }

        attendanceRepository.saveAll(records);
    }

    @Override
    @Transactional
    public void markAbsenteesOnly(Long batchId, LocalDate date, List<Long> absentStudentIds, User markedBy) {
        validateDate(date);
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + batchId));

        List<Student> allStudents = studentRepository.findByBatchId(batchId);
        List<Attendance> records = new ArrayList<>();

        for (Student student : allStudents) {
            Attendance attendance = attendanceRepository
                    .findByStudentIdAndAttendanceDate(student.getId(), date)
                    .orElseGet(Attendance::new);

            attendance.setStudent(student);
            attendance.setBatch(batch);
            attendance.setAttendanceDate(date);
            attendance.setMarkedTime(LocalTime.now());
            attendance.setMarkedBy(markedBy);

            if (absentStudentIds != null && absentStudentIds.contains(student.getId())) {
                attendance.setStatus(Attendance.Status.ABSENT);
            } else {
                attendance.setStatus(Attendance.Status.PRESENT);
            }
            records.add(attendance);
        }

        attendanceRepository.saveAll(records);
    }

    @Override
    public List<Attendance> getAttendanceByBatchAndDate(Long batchId, LocalDate date) {
        return attendanceRepository.findByBatchIdAndAttendanceDate(batchId, date);
    }

    @Override
    public boolean isAttendanceMarked(Long batchId, LocalDate date) {
        List<Attendance> records = getAttendanceByBatchAndDate(batchId, date);
        return records != null && !records.isEmpty();
    }
}
