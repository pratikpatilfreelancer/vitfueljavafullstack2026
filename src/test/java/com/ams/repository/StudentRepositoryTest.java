package com.ams.repository;

import com.ams.entity.Batch;
import com.ams.entity.Student;
import com.ams.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private UserRepository userRepository;

    private Batch batch;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        batchRepository.deleteAll();
        userRepository.deleteAll();

        User teacher = new User("John Smith", "john.smith", "encoded",
                User.Role.TEACHER, "john@ams.com");
        teacher = userRepository.save(teacher);

        batch = new Batch("CS101", "Intro to CS", teacher, "MWF", "09:00-11:00");
        batch = batchRepository.save(batch);
    }

    @Test
    @DisplayName("Should find students by batch ID")
    void findByBatchId() {
        studentRepository.save(new Student("STU001", "Alice", batch, "a@x.com", "123"));
        studentRepository.save(new Student("STU002", "Bob", batch, "b@x.com", "456"));

        List<Student> students = studentRepository.findByBatchId(batch.getId());

        assertThat(students).hasSize(2);
    }

    @Test
    @DisplayName("Should find student by enrollment number")
    void findByEnrollmentNo() {
        studentRepository.save(new Student("STU001", "Alice", batch, "a@x.com", "123"));

        Optional<Student> found = studentRepository.findByEnrollmentNo("STU001");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Should return empty for non-existent enrollment number")
    void findByEnrollmentNo_notFound() {
        Optional<Student> found = studentRepository.findByEnrollmentNo("DOES_NOT_EXIST");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check existence by enrollment number")
    void existsByEnrollmentNo() {
        studentRepository.save(new Student("STU001", "Alice", batch, "a@x.com", "123"));

        assertThat(studentRepository.existsByEnrollmentNo("STU001")).isTrue();
        assertThat(studentRepository.existsByEnrollmentNo("STU999")).isFalse();
    }

    @Test
    @DisplayName("Should count students by batch ID")
    void countByBatchId() {
        studentRepository.save(new Student("STU001", "Alice", batch, "a@x.com", "123"));
        studentRepository.save(new Student("STU002", "Bob", batch, "b@x.com", "456"));

        assertThat(studentRepository.countByBatchId(batch.getId())).isEqualTo(2);
    }

    @Test
    @DisplayName("Should enforce unique enrollment number constraint")
    void uniqueEnrollmentNo() {
        studentRepository.save(new Student("STU001", "Alice", batch, "a@x.com", "123"));

        Student duplicate = new Student("STU001", "Different Name", batch, "d@x.com", "789");

        assertThatThrownBy(() -> {
            studentRepository.saveAndFlush(duplicate);
        }).isInstanceOf(Exception.class);
    }
}
