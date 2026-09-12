package com.example.tourist.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tourists")
public class Tourist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String destination;
    private Double packagePrice;
    private Integer daysOfStay;

    // Default Constructor (required by JPA)
    public Tourist() {
    }

    // Parameterized Constructor
    public Tourist(String name, String email, String destination, Double packagePrice, Integer daysOfStay) {
        this.name = name;
        this.email = email;
        this.destination = destination;
        this.packagePrice = packagePrice;
        this.daysOfStay = daysOfStay;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(Double packagePrice) {
        this.packagePrice = packagePrice;
    }

    public Integer getDaysOfStay() {
        return daysOfStay;
    }

    public void setDaysOfStay(Integer daysOfStay) {
        this.daysOfStay = daysOfStay;
    }
}