package com.foodwaste.foodwastemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Default constructor required by JPA
    public Category() {
    }

    // Constructor
    public Category(String name) {
        this.name = name;
    }

    // Getter
    public Long getId() {
        return id;
    }

    // Setter
    public void setId(Long id) {
        this.id = id;
    }

    // Getter
    public String getName() {
        return name;
    }

    // Setter
    public void setName(String name) {
        this.name = name;
    }
}