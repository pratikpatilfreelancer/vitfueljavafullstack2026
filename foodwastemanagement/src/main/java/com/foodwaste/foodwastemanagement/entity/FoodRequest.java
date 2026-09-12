package com.foodwaste.foodwastemanagement.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "food_requests")
public class FoodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    private LocalDateTime requestDate;

    // The food donation being requested
    @ManyToOne
    @JoinColumn(name = "food_donation_id")
    private FoodDonation foodDonation;

    // The user/NGO making the request
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    // Default constructor required by JPA
    public FoodRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public FoodDonation getFoodDonation() {
        return foodDonation;
    }

    public void setFoodDonation(FoodDonation foodDonation) {
        this.foodDonation = foodDonation;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }
}