package com.example.tourist.service;

import java.util.List;
import com.example.tourist.model.Tourist;

public interface TouristService {
    List<Tourist> getAllTourists();
    void saveTourist(Tourist tourist);
    Tourist getTouristById(Long id);
    void deleteTouristById(Long id);
}