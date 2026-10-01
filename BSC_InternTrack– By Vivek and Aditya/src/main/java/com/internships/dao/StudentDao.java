package com.internships.dao;

import com.internships.model.Student;
import java.util.List;

public interface StudentDao {
    List<Student> findAll();
    Student findById(String studentId);
    Student findByEmail(String email);
    int save(Student student);
    int update(Student student);
    int deleteById(String studentId);
}
