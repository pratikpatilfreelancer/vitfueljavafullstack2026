package com.foodwaste.foodwastemanagement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodwaste.foodwastemanagement.entity.FoodRequest;
import com.foodwaste.foodwastemanagement.service.FoodRequestService;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "http://localhost:5173")
public class FoodRequestController {

    private final FoodRequestService foodRequestService;

    public FoodRequestController(FoodRequestService foodRequestService) {
        this.foodRequestService = foodRequestService;
    }

    // CREATE
    @PostMapping
    public FoodRequest createRequest(
            @RequestParam Long donationId,
            @RequestParam Long userId) {

        return foodRequestService.createRequest(donationId, userId);
    }

    // READ - Get all requests
    @GetMapping
    public List<FoodRequest> getAllRequests() {
        return foodRequestService.getAllRequests();
    }

    // READ - Get requests by user
    @GetMapping("/user/{userId}")
    public List<FoodRequest> getRequestsByUser(
            @PathVariable Long userId) {

        return foodRequestService.getRequestsByUser(userId);
    }

    // UPDATE - Update request status
    @PutMapping("/{id}/status")
    public FoodRequest updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return foodRequestService.updateStatus(id, status);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteRequest(@PathVariable Long id) {

        foodRequestService.deleteRequest(id);

        return "Food request deleted successfully";
    }
}