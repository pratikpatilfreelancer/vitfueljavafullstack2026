package com.ams.config;

import com.ams.entity.*;
import com.ams.repository.AttendanceRepository;
import com.ams.repository.BatchRepository;
import com.ams.repository.StudentRepository;
import com.ams.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the database with default data on first run for easy testing.
 * Only runs if no users exist in the database.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BatchRepository batchRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                       BatchRepository batchRepository,
                       StudentRepository studentRepository,
                       AttendanceRepository attendanceRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.batchRepository = batchRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Only seed if no users exist (first run)
        if (userRepository.count() > 0) {
            return;
        }

        // 1. Create default Admin
        User admin = new User();
        admin.setFullName("System Administrator");
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ADMIN);
        admin.setEmail("admin@ams.com");
        userRepository.save(admin);

        // 2. Create sample Teacher
        User teacher = new User();
        teacher.setFullName("John Smith");
        teacher.setUsername("john.smith");
        teacher.setPassword(passwordEncoder.encode("teacher123"));
        teacher.setRole(User.Role.TEACHER);
        teacher.setEmail("john.smith@ams.com");
        userRepository.save(teacher);

        // 3. Create a second sample Teacher
        User teacher2 = new User();
        teacher2.setFullName("Jane Doe");
        teacher2.setUsername("jane.doe");
        teacher2.setPassword(passwordEncoder.encode("teacher123"));
        teacher2.setRole(User.Role.TEACHER);
        teacher2.setEmail("jane.doe@ams.com");
        userRepository.save(teacher2);

        // 4. Create sample Batches
        Batch batch1 = new Batch();
        batch1.setBatchCode("CS101");
        batch1.setSubject("Introduction to Computer Science");
        batch1.setTeacher(teacher);
        batch1.setBatchDays("MWF");
        batch1.setBatchTiming("09:00-11:00");
        batchRepository.save(batch1);

        Batch batch2 = new Batch();
        batch2.setBatchCode("MATH201");
        batch2.setSubject("Linear Algebra");
        batch2.setTeacher(teacher);
        batch2.setBatchDays("TTS");
        batch2.setBatchTiming("11:00-13:00");
        batchRepository.save(batch2);

        Batch batch3 = new Batch();
        batch3.setBatchCode("PHY101");
        batch3.setSubject("Physics Fundamentals");
        batch3.setTeacher(teacher2);
        batch3.setBatchDays("MWF");
        batch3.setBatchTiming("14:00-16:00");
        batchRepository.save(batch3);

        // 5. Create sample Students
        List<Student> batch1Students = new ArrayList<>();
        String[][] studentData = {
            {"STU001", "Alice Johnson", "alice@example.com", "9876543210"},
            {"STU002", "Bob Williams", "bob@example.com", "9876543211"},
            {"STU003", "Charlie Brown", "charlie@example.com", "9876543212"},
            {"STU004", "Diana Prince", "diana@example.com", "9876543213"},
            {"STU005", "Edward Norton", "edward@example.com", "9876543214"},
        };

        for (String[] data : studentData) {
            Student student = new Student();
            student.setEnrollmentNo(data[0]);
            student.setName(data[1]);
            student.setBatch(batch1);
            student.setEmail(data[2]);
            student.setPhone(data[3]);
            batch1Students.add(studentRepository.save(student));
        }

        // Add students to batch2 & batch3
        List<Student> batch2Students = new ArrayList<>();
        Student s6 = studentRepository.save(new Student("STU006", "Fiona Apple", batch2, "fiona@example.com", "9876543215"));
        Student s7 = studentRepository.save(new Student("STU007", "George Martin", batch2, "george@example.com", "9876543216"));
        batch2Students.add(s6);
        batch2Students.add(s7);

        List<Student> batch3Students = new ArrayList<>();
        Student s8 = studentRepository.save(new Student("STU008", "Helen Troy", batch3, "helen@example.com", "9876543217"));
        Student s9 = studentRepository.save(new Student("STU009", "Ivan Petrov", batch3, "ivan@example.com", "9876543218"));
        batch3Students.add(s8);
        batch3Students.add(s9);

        // 6. Seed Attendance Records for the past 10 days
        LocalDate today = LocalDate.now();
        List<Attendance> attendanceList = new ArrayList<>();

        for (int i = 10; i >= 1; i--) {
            LocalDate date = today.minusDays(i);

            // Batch 1 (CS101) - Mark attendance
            for (int sIdx = 0; sIdx < batch1Students.size(); sIdx++) {
                Student s = batch1Students.get(sIdx);
                // Make Edward (index 4) miss most classes to test < 75% threshold
                boolean isPresent = (sIdx == 4) ? (i % 3 == 0) : (i != 4); 
                Attendance.Status status = isPresent ? Attendance.Status.PRESENT : Attendance.Status.ABSENT;
                attendanceList.add(new Attendance(s, batch1, date, LocalTime.of(9, 15), status, teacher));
            }

            // Batch 2 (MATH201)
            for (Student s : batch2Students) {
                boolean isPresent = (s.getEnrollmentNo().equals("STU007")) ? (i % 2 == 0) : true;
                Attendance.Status status = isPresent ? Attendance.Status.PRESENT : Attendance.Status.ABSENT;
                attendanceList.add(new Attendance(s, batch2, date, LocalTime.of(11, 10), status, teacher));
            }

            // Batch 3 (PHY101)
            for (Student s : batch3Students) {
                Attendance.Status status = (i % 2 == 0) ? Attendance.Status.PRESENT : Attendance.Status.ABSENT;
                attendanceList.add(new Attendance(s, batch3, date, LocalTime.of(14, 5), status, teacher2));
            }
        }

        attendanceRepository.saveAll(attendanceList);

        System.out.println("===================================================");
        System.out.println("  DATA SEEDER: Sample data created successfully!");
        System.out.println("  Admin login:    admin / admin123");
        System.out.println("  Teacher login:  john.smith / teacher123");
        System.out.println("  Teacher login:  jane.doe / teacher123");
        System.out.println("  Attendance records seeded for past 10 days.");
        System.out.println("===================================================");
    }
}
