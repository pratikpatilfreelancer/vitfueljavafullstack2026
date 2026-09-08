package com.example.tourist.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tourist.model.Tourist;
import com.example.tourist.repository.TouristRepository;

@Service
public class TouristServiceImpl implements TouristService {

    @Autowired
    private TouristRepository touristRepository;

    @Override
    public List<Tourist> getAllTourists() {
        return touristRepository.findAll();
    }

    @Override
    public void saveTourist(Tourist tourist) {
        touristRepository.save(tourist);
    }

    @Override
    public Tourist getTouristById(Long id) {
        Optional<Tourist> optional = touristRepository.findById(id);
        Tourist tourist = null;
        if (optional.isPresent()) {
            tourist = optional.get();
        } else {
            throw new RuntimeException("Tourist not found for id :: " + id);
        }
        return tourist;
    }

    @Override
    public void deleteTouristById(Long id) {
        touristRepository.deleteById(id);
    }
}