package com.foodwaste.foodwastemanagement.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foodwaste.foodwastemanagement.entity.FoodDonation;
import com.foodwaste.foodwastemanagement.entity.FoodRequest;
import com.foodwaste.foodwastemanagement.entity.User;
import com.foodwaste.foodwastemanagement.repository.FoodDonationRepository;
import com.foodwaste.foodwastemanagement.repository.FoodRequestRepository;
import com.foodwaste.foodwastemanagement.repository.UserRepository;

@Service
public class FoodRequestService {

    private final FoodRequestRepository foodRequestRepository;
    private final FoodDonationRepository foodDonationRepository;
    private final UserRepository userRepository;

    public FoodRequestService(
            FoodRequestRepository foodRequestRepository,
            FoodDonationRepository foodDonationRepository,
            UserRepository userRepository) {

        this.foodRequestRepository = foodRequestRepository;
        this.foodDonationRepository = foodDonationRepository;
        this.userRepository = userRepository;
    }

    // CREATE REQUEST
    public FoodRequest createRequest(Long donationId, Long userId) {

        FoodDonation donation = foodDonationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Food donation not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check whether the donation is available
        if (!"AVAILABLE".equalsIgnoreCase(donation.getStatus())) {
            throw new RuntimeException("This food donation is no longer available");
        }

        // Prevent duplicate requests
        if (foodRequestRepository.existsByFoodDonationIdAndRequesterId(
                donationId, userId)) {

            throw new RuntimeException(
                    "You have already requested this food donation");
        }

        FoodRequest request = new FoodRequest();

        request.setFoodDonation(donation);
        request.setRequester(user);
        request.setStatus("PENDING");
        request.setRequestDate(LocalDateTime.now());

        return foodRequestRepository.save(request);
    }

    // READ - Get all requests
    public List<FoodRequest> getAllRequests() {
        return foodRequestRepository.findAll();
    }

    // READ - Get requests by user
    public List<FoodRequest> getRequestsByUser(Long userId) {
        return foodRequestRepository.findByRequesterId(userId);
    }

    // UPDATE REQUEST STATUS
    public FoodRequest updateStatus(Long id, String status) {

        FoodRequest request = foodRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food request not found"));

        request.setStatus(status);

        // If request is approved, mark food as claimed
        if ("APPROVED".equalsIgnoreCase(status)) {
            FoodDonation donation = request.getFoodDonation();
            donation.setStatus("CLAIMED");
            foodDonationRepository.save(donation);
        }

        return foodRequestRepository.save(request);
    }

    // DELETE
    public void deleteRequest(Long id) {

        if (!foodRequestRepository.existsById(id)) {
            throw new RuntimeException("Food request not found");
        }

        foodRequestRepository.deleteById(id);
    }
}