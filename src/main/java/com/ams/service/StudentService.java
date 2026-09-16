package com.ams.service;

import com.ams.dto.StudentDto;
import com.ams.entity.Student;
import java.util.List;

public interface StudentService {
    List<Student> findAll();
    List<Student> findByBatchId(Long batchId);
    Student findById(Long id);
    Student save(StudentDto dto);
    Student update(Long id, StudentDto dto);
    void delete(Long id);
    long count();
}
