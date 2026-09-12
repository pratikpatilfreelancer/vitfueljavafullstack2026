package com.foodwaste.foodwastemanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodwaste.foodwastemanagement.entity.FoodRequest;

public interface FoodRequestRepository extends JpaRepository<FoodRequest, Long> {

    List<FoodRequest> findByRequesterId(Long requesterId);

    boolean existsByFoodDonationIdAndRequesterId(Long foodDonationId, Long requesterId);
}