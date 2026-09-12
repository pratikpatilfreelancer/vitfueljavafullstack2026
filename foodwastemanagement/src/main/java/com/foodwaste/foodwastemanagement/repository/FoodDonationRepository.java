package com.foodwaste.foodwastemanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodwaste.foodwastemanagement.entity.FoodDonation;

public interface FoodDonationRepository
        extends JpaRepository<FoodDonation, Long> {

    List<FoodDonation> findByStatus(String status);

    List<FoodDonation> findByDonorId(Long donorId);
}