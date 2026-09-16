package com.ams.repository;

import com.ams.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStudentIdAndAttendanceDate(Long studentId, LocalDate date);

    List<Attendance> findByBatchIdAndAttendanceDate(Long batchId, LocalDate date);

    List<Attendance> findByBatchIdAndAttendanceDateBetween(Long batchId, LocalDate start, LocalDate end);

    List<Attendance> findByStudentIdAndAttendanceDateBetween(Long studentId, LocalDate start, LocalDate end);

    boolean existsByStudentIdAndAttendanceDate(Long studentId, LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.status = 'PRESENT' " +
           "AND a.attendanceDate BETWEEN :start AND :end")
    long countPresentByStudentAndDateRange(@Param("studentId") Long studentId,
                                           @Param("start") LocalDate start,
                                           @Param("end") LocalDate end);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId " +
           "AND a.attendanceDate BETWEEN :start AND :end")
    long countTotalByStudentAndDateRange(@Param("studentId") Long studentId,
                                         @Param("start") LocalDate start,
                                         @Param("end") LocalDate end);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.batch.id = :batchId AND a.status = 'PRESENT' " +
           "AND a.attendanceDate BETWEEN :start AND :end")
    long countPresentByBatchAndDateRange(@Param("batchId") Long batchId,
                                         @Param("start") LocalDate start,
                                         @Param("end") LocalDate end);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.batch.id = :batchId " +
           "AND a.attendanceDate BETWEEN :start AND :end")
    long countTotalByBatchAndDateRange(@Param("batchId") Long batchId,
                                       @Param("start") LocalDate start,
                                       @Param("end") LocalDate end);

    @Query("SELECT DISTINCT a.attendanceDate FROM Attendance a WHERE a.batch.id = :batchId " +
           "AND a.attendanceDate BETWEEN :start AND :end ORDER BY a.attendanceDate")
    List<LocalDate> findDistinctDatesByBatchAndRange(@Param("batchId") Long batchId,
                                                      @Param("start") LocalDate start,
                                                      @Param("end") LocalDate end);
}
