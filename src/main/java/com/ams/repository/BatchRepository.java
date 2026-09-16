package com.ams.repository;

import com.ams.entity.Batch;
import com.ams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    List<Batch> findByTeacher(User teacher);

    List<Batch> findByTeacherId(Long teacherId);

    Optional<Batch> findByBatchCode(String batchCode);

    boolean existsByBatchCode(String batchCode);
}
