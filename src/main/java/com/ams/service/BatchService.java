package com.ams.service;

import com.ams.dto.BatchDto;
import com.ams.entity.Batch;
import java.util.List;

public interface BatchService {
    List<Batch> findAll();
    List<Batch> findByTeacherId(Long teacherId);
    Batch findById(Long id);
    Batch save(BatchDto dto);
    Batch update(Long id, BatchDto dto);
    void delete(Long id);
    long count();
}
