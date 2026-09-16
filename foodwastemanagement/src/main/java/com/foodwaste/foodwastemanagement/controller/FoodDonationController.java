package com.foodwaste.foodwastemanagement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.foodwaste.foodwastemanagement.entity.FoodDonation;
import com.foodwaste.foodwastemanagement.service.FoodDonationService;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "http://localhost:5173")
public class FoodDonationController {

    private final FoodDonationService foodDonationService;

    public FoodDonationController(FoodDonationService foodDonationService) {
        this.foodDonationService = foodDonationService;
    }

    // CREATE
    @PostMapping
    public FoodDonation createDonation(
            @Valid @RequestBody FoodDonation donation) {

        return foodDonationService.createDonation(donation);
    }

    // READ - Get all donations
    @GetMapping
    public List<FoodDonation> getAllDonations() {
        return foodDonationService.getAllDonations();
    }

    // READ - Get donation by ID
    @GetMapping("/{id}")
    public FoodDonation getDonationById(@PathVariable Long id) {
        return foodDonationService.getDonationById(id);
    }

    // READ - Get available donations
    @GetMapping("/available")
    public List<FoodDonation> getAvailableDonations() {
        return foodDonationService.getAvailableDonations();
    }

    // READ - Get donations by donor
    @GetMapping("/donor/{donorId}")
    public List<FoodDonation> getDonationsByDonor(
            @PathVariable Long donorId) {

        return foodDonationService.getDonationsByDonor(donorId);
    }

    // UPDATE
    @PutMapping("/{id}")
    public FoodDonation updateDonation(
            @PathVariable Long id,
            @Valid @RequestBody FoodDonation donation) {

        return foodDonationService.updateDonation(id, donation);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteDonation(@PathVariable Long id) {

        foodDonationService.deleteDonation(id);

        return "Food donation deleted successfully";
    }
}