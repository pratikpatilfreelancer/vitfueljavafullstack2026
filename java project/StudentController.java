package studentperformance;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {


    private final StudentRepository studentRepository;


    // Constructor

    public StudentController(
            StudentRepository studentRepository) {

        this.studentRepository =
                studentRepository;

    }


    // ==========================================
    // POST API
    // Add a new student
    // ==========================================

    @PostMapping

    public Student addStudent(
            @RequestBody Student student) {


        // Validate marks

        if (student.getMath() < 0 ||
                student.getMath() > 100 ||

            student.getScience() < 0 ||
                student.getScience() > 100 ||

            student.getEnglish() < 0 ||
                student.getEnglish() > 100) {

            throw new IllegalArgumentException(
                    "Marks must be between 0 and 100."
            );

        }


        // Calculate total

        double total =
                student.getMath()
                + student.getScience()
                + student.getEnglish();


        // Calculate average

        double average =
                total / 3.0;


        // Calculate grade

        String grade =
                calculateGrade(average);


        // Store calculated values

        student.setTotal(total);

        student.setAverage(average);

        student.setGrade(grade);


        // Save student to MySQL

        return studentRepository.save(student);

    }


    // ==========================================
    // GET API
    // Get all students
    // ==========================================

    @GetMapping

    public List<Student> getAllStudents() {

        return studentRepository.findAll();

    }


    // ==========================================
    // GET API
    // Get one student by ID
    // ==========================================

    @GetMapping("/{id}")

    public Student getStudent(
            @PathVariable Long id) {

        return studentRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Student not found"
                        )
                );

    }


    // ==========================================
    // DELETE API
    // Delete student
    // ==========================================

    @DeleteMapping("/{id}")

    public String deleteStudent(
            @PathVariable Long id) {


        if (!studentRepository.existsById(id)) {

            return "Student not found.";

        }


        studentRepository.deleteById(id);


        return "Student deleted successfully.";

    }


    // ==========================================
    // Grade calculation
    // ==========================================

    private String calculateGrade(
            double average) {


        if (average >= 90) {

            return "A+";

        }


        if (average >= 80) {

            return "A";

        }


        if (average >= 70) {

            return "B";

        }


        if (average >= 60) {

            return "C";

        }


        if (average >= 50) {

            return "D";

        }


        return "F";

    }

}