package com.internships.service;

import com.internships.model.Student;
import java.util.List;

public interface StudentService {
    List<Student> getAllStudents();
    Student getStudentById(String studentId);
    Student getStudentByEmail(String email);
    Student createStudent(Student student);
    Student updateStudent(String studentId, Student student);
    void deleteStudent(String studentId);
}
