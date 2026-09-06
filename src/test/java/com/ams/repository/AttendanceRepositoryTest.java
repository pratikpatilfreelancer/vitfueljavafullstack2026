package com.ams.repository;

import com.ams.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AttendanceRepositoryTest {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private StudentRepository studentRepository;

    private User teacher;
    private Batch batch;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll();
        studentRepository.deleteAll();
        batchRepository.deleteAll();
        userRepository.deleteAll();

        teacher = new User("John Smith", "john.smith", "encoded", User.Role.TEACHER, "john@ams.com");
        teacher = userRepository.save(teacher);

        batch = new Batch("CS101", "Intro to CS", teacher, "MWF", "09:00-11:00");
        batch = batchRepository.save(batch);

        student1 = new Student("STU001", "Alice Johnson", batch, "alice@example.com", "1234567890");
        student1 = studentRepository.save(student1);

        student2 = new Student("STU002", "Bob Williams", batch, "bob@example.com", "1234567891");
        student2 = studentRepository.save(student2);
    }

    @Test
    @DisplayName("Should find attendance by batch and date")
    void findByBatchIdAndAttendanceDate() {
        LocalDate today = LocalDate.now();
        attendanceRepository.save(new Attendance(student1, batch, today, LocalTime.now(),
                Attendance.Status.PRESENT, teacher));
        attendanceRepository.save(new Attendance(student2, batch, today, LocalTime.now(),
                Attendance.Status.ABSENT, teacher));

        List<Attendance> records = attendanceRepository.findByBatchIdAndAttendanceDate(batch.getId(), today);

        assertThat(records).hasSize(2);
    }

    @Test
    @DisplayName("Should check if attendance exists by student and date")
    void existsByStudentIdAndAttendanceDate() {
        LocalDate today = LocalDate.now();
        attendanceRepository.save(new Attendance(student1, batch, today, LocalTime.now(),
                Attendance.Status.PRESENT, teacher));

        assertThat(attendanceRepository.existsByStudentIdAndAttendanceDate(student1.getId(), today)).isTrue();
        assertThat(attendanceRepository.existsByStudentIdAndAttendanceDate(student2.getId(), today)).isFalse();
    }

    @Test
    @DisplayName("Should count present records within date range")
    void countPresentByStudentAndDateRange() {
        LocalDate date1 = LocalDate.now().minusDays(2);
        LocalDate date2 = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();

        attendanceRepository.save(new Attendance(student1, batch, date1, LocalTime.now(),
                Attendance.Status.PRESENT, teacher));
        attendanceRepository.save(new Attendance(student1, batch, date2, LocalTime.now(),
                Attendance.Status.ABSENT, teacher));
        attendanceRepository.save(new Attendance(student1, batch, today, LocalTime.now(),
                Attendance.Status.PRESENT, teacher));

        long presentCount = attendanceRepository.countPresentByStudentAndDateRange(
                student1.getId(), date1, today);
        long totalCount = attendanceRepository.countTotalByStudentAndDateRange(
                student1.getId(), date1, today);

        assertThat(presentCount).isEqualTo(2);
        assertThat(totalCount).isEqualTo(3);
    }

    @Test
    @DisplayName("Should find attendance within date range for a batch")
    void findByBatchIdAndAttendanceDateBetween() {
        LocalDate date1 = LocalDate.now().minusDays(3);
        LocalDate date2 = LocalDate.now().minusDays(1);

        attendanceRepository.save(new Attendance(student1, batch, date1, LocalTime.now(),
                Attendance.Status.PRESENT, teacher));
        attendanceRepository.save(new Attendance(student1, batch, date2, LocalTime.now(),
                Attendance.Status.ABSENT, teacher));

        List<Attendance> records = attendanceRepository.findByBatchIdAndAttendanceDateBetween(
                batch.getId(), date1, date2);

        assertThat(records).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty when no attendance exists")
    void findByBatchIdAndAttendanceDate_noRecords() {
        List<Attendance> records = attendanceRepository.findByBatchIdAndAttendanceDate(
                batch.getId(), LocalDate.now());

        assertThat(records).isEmpty();
    }

    @Test
    @DisplayName("Should enforce unique constraint on student + date")
    void uniqueConstraint_studentAndDate() {
        LocalDate today = LocalDate.now();
        attendanceRepository.save(new Attendance(student1, batch, today, LocalTime.now(),
                Attendance.Status.PRESENT, teacher));

        Attendance duplicate = new Attendance(student1, batch, today, LocalTime.now(),
                Attendance.Status.ABSENT, teacher);

        assertThatThrownBy(() -> {
            attendanceRepository.saveAndFlush(duplicate);
        }).isInstanceOf(Exception.class);
    }
}
