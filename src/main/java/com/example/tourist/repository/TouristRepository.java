package com.example.tourist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.tourist.model.Tourist;

@Repository
public interface TouristRepository extends JpaRepository<Tourist, Long> {
    // No custom SQL needed! JpaRepository gives all CRUD methods out of the box.
}