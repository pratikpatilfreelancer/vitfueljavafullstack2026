package com.ams.repository;

import com.ams.entity.Batch;
import com.ams.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByBatch(Batch batch);

    List<Student> findByBatchId(Long batchId);

    Optional<Student> findByEnrollmentNo(String enrollmentNo);

    boolean existsByEnrollmentNo(String enrollmentNo);

    long countByBatchId(Long batchId);
}
