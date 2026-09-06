package com.ams.service;

import com.ams.entity.*;
import com.ams.exception.FutureDateException;
import com.ams.repository.AttendanceRepository;
import com.ams.repository.BatchRepository;
import com.ams.repository.StudentRepository;
import com.ams.service.impl.AttendanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private User teacher;
    private Batch batch;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        teacher = new User("John Smith", "john.smith", "encoded", User.Role.TEACHER, "john@ams.com");
        teacher.setId(1L);

        batch = new Batch("CS101", "Intro to CS", teacher, "MWF", "09:00-11:00");
        batch.setId(1L);

        student1 = new Student("STU001", "Alice Johnson", batch, "alice@example.com", "1234567890");
        student1.setId(1L);

        student2 = new Student("STU002", "Bob Williams", batch, "bob@example.com", "1234567891");
        student2.setId(2L);
    }

    @Test
    @DisplayName("Should reject attendance marking for a future date")
    void markBatchAttendance_futureDate_throwsFutureDateException() {
        LocalDate futureDate = LocalDate.now().plusDays(1);

        assertThrows(FutureDateException.class, () ->
                attendanceService.markBatchAttendance(1L, futureDate, List.of(1L), teacher));
    }

    @Test
    @DisplayName("Should reject absentees marking for a future date")
    void markAbsenteesOnly_futureDate_throwsFutureDateException() {
        LocalDate futureDate = LocalDate.now().plusDays(1);

        assertThrows(FutureDateException.class, () ->
                attendanceService.markAbsenteesOnly(1L, futureDate, List.of(1L), teacher));
    }

    @Test
    @DisplayName("Should update existing attendance record when re-marked for the same date")
    void markBatchAttendance_existingRecord_updatesCorrectly() {
        LocalDate today = LocalDate.now();
        Attendance existing = new Attendance(student1, batch, today, java.time.LocalTime.now(), Attendance.Status.ABSENT, teacher);
        existing.setId(10L);

        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(studentRepository.findByBatchId(1L)).thenReturn(List.of(student1));
        when(attendanceRepository.findByStudentIdAndAttendanceDate(1L, today)).thenReturn(Optional.of(existing));

        attendanceService.markBatchAttendance(1L, today, List.of(1L), teacher);

        verify(attendanceRepository).saveAll(argThat(records -> {
            List<Attendance> list = (List<Attendance>) records;
            return list.size() == 1 &&
                   list.get(0).getId().equals(10L) &&
                   list.get(0).getStatus() == Attendance.Status.PRESENT;
        }));
    }

    @Test
    @DisplayName("Should mark all students as PRESENT when all IDs are in presentStudentIds")
    void markBatchAttendance_allPresent_savesCorrectly() {
        LocalDate today = LocalDate.now();
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(studentRepository.findByBatchId(1L)).thenReturn(Arrays.asList(student1, student2));
        when(attendanceRepository.findByStudentIdAndAttendanceDate(anyLong(), eq(today))).thenReturn(Optional.empty());

        attendanceService.markBatchAttendance(1L, today, List.of(1L, 2L), teacher);

        verify(attendanceRepository).saveAll(argThat(records -> {
            List<Attendance> list = (List<Attendance>) records;
            return list.size() == 2 &&
                   list.stream().allMatch(a -> a.getStatus() == Attendance.Status.PRESENT);
        }));
    }

    @Test
    @DisplayName("Should mark unchecked students as ABSENT in batch attendance")
    void markBatchAttendance_someAbsent_savesCorrectly() {
        LocalDate today = LocalDate.now();
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(studentRepository.findByBatchId(1L)).thenReturn(Arrays.asList(student1, student2));
        when(attendanceRepository.findByStudentIdAndAttendanceDate(anyLong(), eq(today))).thenReturn(Optional.empty());

        // Only student1 is present
        attendanceService.markBatchAttendance(1L, today, List.of(1L), teacher);

        verify(attendanceRepository).saveAll(argThat(records -> {
            List<Attendance> list = (List<Attendance>) records;
            boolean s1Present = list.stream()
                    .filter(a -> a.getStudent().getId().equals(1L))
                    .allMatch(a -> a.getStatus() == Attendance.Status.PRESENT);
            boolean s2Absent = list.stream()
                    .filter(a -> a.getStudent().getId().equals(2L))
                    .allMatch(a -> a.getStatus() == Attendance.Status.ABSENT);
            return list.size() == 2 && s1Present && s2Absent;
        }));
    }

    @Test
    @DisplayName("Should mark selected students ABSENT and others PRESENT in absentees-only mode")
    void markAbsenteesOnly_savesCorrectly() {
        LocalDate today = LocalDate.now();
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(studentRepository.findByBatchId(1L)).thenReturn(Arrays.asList(student1, student2));
        when(attendanceRepository.findByStudentIdAndAttendanceDate(anyLong(), eq(today))).thenReturn(Optional.empty());

        // student2 is absent
        attendanceService.markAbsenteesOnly(1L, today, List.of(2L), teacher);

        verify(attendanceRepository).saveAll(argThat(records -> {
            List<Attendance> list = (List<Attendance>) records;
            boolean s1Present = list.stream()
                    .filter(a -> a.getStudent().getId().equals(1L))
                    .allMatch(a -> a.getStatus() == Attendance.Status.PRESENT);
            boolean s2Absent = list.stream()
                    .filter(a -> a.getStudent().getId().equals(2L))
                    .allMatch(a -> a.getStatus() == Attendance.Status.ABSENT);
            return list.size() == 2 && s1Present && s2Absent;
        }));
    }

    @Test
    @DisplayName("Should correctly report if attendance is already marked")
    void isAttendanceMarked_returnsCorrectly() {
        LocalDate today = LocalDate.now();
        Attendance a = new Attendance(student1, batch, today, java.time.LocalTime.now(),
                Attendance.Status.PRESENT, teacher);

        when(attendanceRepository.findByBatchIdAndAttendanceDate(1L, today))
                .thenReturn(List.of(a));

        assertTrue(attendanceService.isAttendanceMarked(1L, today));
    }

    @Test
    @DisplayName("Should return false when no attendance is marked")
    void isAttendanceMarked_noRecords_returnsFalse() {
        LocalDate today = LocalDate.now();
        when(attendanceRepository.findByBatchIdAndAttendanceDate(1L, today))
                .thenReturn(List.of());

        assertFalse(attendanceService.isAttendanceMarked(1L, today));
    }

    @Test
    @DisplayName("Should allow marking attendance for today's date")
    void markBatchAttendance_today_succeeds() {
        LocalDate today = LocalDate.now();
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(studentRepository.findByBatchId(1L)).thenReturn(List.of(student1));
        when(attendanceRepository.findByStudentIdAndAttendanceDate(anyLong(), eq(today))).thenReturn(Optional.empty());

        assertDoesNotThrow(() ->
                attendanceService.markBatchAttendance(1L, today, List.of(1L), teacher));
    }

    @Test
    @DisplayName("Should allow marking attendance for a past date")
    void markBatchAttendance_pastDate_succeeds() {
        LocalDate pastDate = LocalDate.now().minusDays(5);
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(studentRepository.findByBatchId(1L)).thenReturn(List.of(student1));
        when(attendanceRepository.findByStudentIdAndAttendanceDate(anyLong(), eq(pastDate))).thenReturn(Optional.empty());

        assertDoesNotThrow(() ->
                attendanceService.markBatchAttendance(1L, pastDate, List.of(1L), teacher));
    }
}
