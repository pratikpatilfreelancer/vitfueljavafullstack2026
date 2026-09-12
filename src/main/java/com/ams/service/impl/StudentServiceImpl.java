package com.ams.service.impl;

import com.ams.dto.StudentDto;
import com.ams.entity.Batch;
import com.ams.entity.Student;
import com.ams.exception.DuplicateRecordException;
import com.ams.exception.ResourceNotFoundException;
import com.ams.repository.BatchRepository;
import com.ams.repository.StudentRepository;
import com.ams.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;

    public StudentServiceImpl(StudentRepository studentRepository, BatchRepository batchRepository) {
        this.studentRepository = studentRepository;
        this.batchRepository = batchRepository;
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> findByBatchId(Long batchId) {
        return studentRepository.findByBatchId(batchId);
    }

    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Override
    public Student save(StudentDto dto) {
        if (studentRepository.existsByEnrollmentNo(dto.getEnrollmentNo())) {
            throw new DuplicateRecordException("Enrollment number already exists: " + dto.getEnrollmentNo());
        }
        Batch batch = batchRepository.findById(dto.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + dto.getBatchId()));

        Student student = new Student();
        student.setEnrollmentNo(dto.getEnrollmentNo());
        student.setName(dto.getName());
        student.setBatch(batch);
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        
        return studentRepository.save(student);
    }

    @Override
    public Student update(Long id, StudentDto dto) {
        Student student = findById(id);
        if (!student.getEnrollmentNo().equals(dto.getEnrollmentNo()) && studentRepository.existsByEnrollmentNo(dto.getEnrollmentNo())) {
            throw new DuplicateRecordException("Enrollment number already exists: " + dto.getEnrollmentNo());
        }
        Batch batch = batchRepository.findById(dto.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + dto.getBatchId()));

        student.setEnrollmentNo(dto.getEnrollmentNo());
        student.setName(dto.getName());
        student.setBatch(batch);
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        
        return studentRepository.save(student);
    }

    @Override
    public void delete(Long id) {
        Student student = findById(id);
        studentRepository.delete(student);
    }

    @Override
    public long count() {
        return studentRepository.count();
    }
}
