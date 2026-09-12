package com.ams.service;

import com.ams.entity.Attendance;
import com.ams.entity.User;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    void markBatchAttendance(Long batchId, LocalDate date, List<Long> presentStudentIds, User markedBy);
    void markAbsenteesOnly(Long batchId, LocalDate date, List<Long> absentStudentIds, User markedBy);
    List<Attendance> getAttendanceByBatchAndDate(Long batchId, LocalDate date);
    boolean isAttendanceMarked(Long batchId, LocalDate date);
}
