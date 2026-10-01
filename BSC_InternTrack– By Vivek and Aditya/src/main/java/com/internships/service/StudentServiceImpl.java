package com.internships.service;

import com.internships.dao.StudentDao;
import com.internships.exception.DuplicateResourceException;
import com.internships.exception.NotFoundException;
import com.internships.model.Student;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentDao studentDao;

    public StudentServiceImpl(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentDao.findAll();
    }

    @Override
    public Student getStudentById(String studentId) {
        Student student = studentDao.findById(studentId);
        if (student == null) {
            throw new NotFoundException("Student not found with id: " + studentId);
        }
        return student;
    }

    @Override
    public Student getStudentByEmail(String email) {
        Student student = studentDao.findByEmail(email);
        if (student == null) {
            throw new NotFoundException("Student not found with email: " + email);
        }
        return student;
    }

    @Override
    public Student createStudent(Student student) {
        if (student.getStudentId() == null || student.getStudentId().isBlank()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (student.getName() == null || student.getName().isBlank()) {
            throw new IllegalArgumentException("Student name is required");
        }
        if (student.getEmail() == null || student.getEmail().isBlank()) {
            throw new IllegalArgumentException("Student email is required");
        }
        if (studentDao.findByEmail(student.getEmail()) != null) {
            throw new DuplicateResourceException("Student with email already exists: " + student.getEmail());
        }
        if (studentDao.findById(student.getStudentId()) != null) {
            throw new DuplicateResourceException("Student with id already exists: " + student.getStudentId());
        }
        studentDao.save(student);
        return studentDao.findById(student.getStudentId());
    }

    @Override
    public Student updateStudent(String studentId, Student student) {
        Student existing = studentDao.findById(studentId);
        if (existing == null) {
            throw new NotFoundException("Student not found with id: " + studentId);
        }
        if (student.getEmail() == null || student.getEmail().isBlank()) {
            throw new IllegalArgumentException("Student email is required");
        }
        Student byEmail = studentDao.findByEmail(student.getEmail());
        if (byEmail != null && !byEmail.getStudentId().equals(studentId)) {
            throw new DuplicateResourceException("Student with email already exists: " + student.getEmail());
        }
        student.setStudentId(studentId);
        studentDao.update(student);
        return studentDao.findById(studentId);
    }

    @Override
    public void deleteStudent(String studentId) {
        Student existing = studentDao.findById(studentId);
        if (existing == null) {
            throw new NotFoundException("Student not found with id: " + studentId);
        }
        studentDao.deleteById(studentId);
    }
}
