package com.capgemini.apartment_maintenance.repository;

import com.capgemini.apartment_maintenance.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Building entity.
 * Handles basic CRUD via JpaRepository — no custom queries needed yet.
 */
public interface BuildingRepository extends JpaRepository<Building, Integer> {
}