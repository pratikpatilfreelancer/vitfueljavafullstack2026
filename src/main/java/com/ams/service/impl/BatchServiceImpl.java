package com.ams.service.impl;

import com.ams.dto.BatchDto;
import com.ams.entity.Batch;
import com.ams.entity.User;
import com.ams.exception.DuplicateRecordException;
import com.ams.exception.ResourceNotFoundException;
import com.ams.repository.BatchRepository;
import com.ams.repository.UserRepository;
import com.ams.service.BatchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;
    private final UserRepository userRepository;

    public BatchServiceImpl(BatchRepository batchRepository, UserRepository userRepository) {
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Batch> findAll() {
        return batchRepository.findAll();
    }

    @Override
    public List<Batch> findByTeacherId(Long teacherId) {
        return batchRepository.findByTeacherId(teacherId);
    }

    @Override
    public Batch findById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + id));
    }

    @Override
    public Batch save(BatchDto dto) {
        if (batchRepository.existsByBatchCode(dto.getBatchCode())) {
            throw new DuplicateRecordException("Batch code already exists: " + dto.getBatchCode());
        }
        User teacher = userRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + dto.getTeacherId()));

        Batch batch = new Batch();
        batch.setBatchCode(dto.getBatchCode());
        batch.setSubject(dto.getSubject());
        batch.setTeacher(teacher);
        batch.setBatchDays(dto.getBatchDays());
        batch.setBatchTiming(dto.getBatchTiming());
        
        return batchRepository.save(batch);
    }

    @Override
    public Batch update(Long id, BatchDto dto) {
        Batch batch = findById(id);
        if (!batch.getBatchCode().equals(dto.getBatchCode()) && batchRepository.existsByBatchCode(dto.getBatchCode())) {
            throw new DuplicateRecordException("Batch code already exists: " + dto.getBatchCode());
        }
        User teacher = userRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + dto.getTeacherId()));

        batch.setBatchCode(dto.getBatchCode());
        batch.setSubject(dto.getSubject());
        batch.setTeacher(teacher);
        batch.setBatchDays(dto.getBatchDays());
        batch.setBatchTiming(dto.getBatchTiming());
        
        return batchRepository.save(batch);
    }

    @Override
    public void delete(Long id) {
        Batch batch = findById(id);
        batchRepository.delete(batch);
    }

    @Override
    public long count() {
        return batchRepository.count();
    }
}
