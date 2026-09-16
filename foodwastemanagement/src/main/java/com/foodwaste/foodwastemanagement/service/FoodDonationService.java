package com.foodwaste.foodwastemanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodwaste.foodwastemanagement.entity.FoodDonation;
import com.foodwaste.foodwastemanagement.repository.FoodDonationRepository;

@Service
public class FoodDonationService {

    private final FoodDonationRepository foodDonationRepository;

    public FoodDonationService(FoodDonationRepository foodDonationRepository) {
        this.foodDonationRepository = foodDonationRepository;
    }

    // CREATE
    public FoodDonation createDonation(FoodDonation donation) {

        if (donation.getStatus() == null || donation.getStatus().isBlank()) {
            donation.setStatus("AVAILABLE");
        }

        return foodDonationRepository.save(donation);
    }

    // READ - Get all donations
    public List<FoodDonation> getAllDonations() {
        return foodDonationRepository.findAll();
    }

    // READ - Get donation by ID
    public FoodDonation getDonationById(Long id) {
        return foodDonationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food donation not found"));
    }

    // READ - Get available donations
    public List<FoodDonation> getAvailableDonations() {
        return foodDonationRepository.findByStatus("AVAILABLE");
    }

    // READ - Get donations by donor
    public List<FoodDonation> getDonationsByDonor(Long donorId) {
        return foodDonationRepository.findByDonorId(donorId);
    }

    // UPDATE
    public FoodDonation updateDonation(Long id, FoodDonation updatedDonation) {

        FoodDonation existingDonation = foodDonationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food donation not found"));

        existingDonation.setFoodName(updatedDonation.getFoodName());
        existingDonation.setDescription(updatedDonation.getDescription());
        existingDonation.setQuantity(updatedDonation.getQuantity());
        existingDonation.setExpiryDate(updatedDonation.getExpiryDate());
        existingDonation.setLocation(updatedDonation.getLocation());
        existingDonation.setStatus(updatedDonation.getStatus());
        existingDonation.setDonor(updatedDonation.getDonor());
        existingDonation.setCategory(updatedDonation.getCategory());

        return foodDonationRepository.save(existingDonation);
    }

    // DELETE
    public void deleteDonation(Long id) {

        if (!foodDonationRepository.existsById(id)) {
            throw new RuntimeException("Food donation not found");
        }

        foodDonationRepository.deleteById(id);
    }
}