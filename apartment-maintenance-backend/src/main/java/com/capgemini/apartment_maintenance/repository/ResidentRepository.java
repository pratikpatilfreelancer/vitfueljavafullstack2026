package com.capgemini.apartment_maintenance.repository;

import com.capgemini.apartment_maintenance.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Resident entity.
 * Provides CRUD plus lookups by building and email.
 */
public interface ResidentRepository extends JpaRepository<Resident, Integer> {

    // all residents living in a given building — building_id is the FK column
    List<Resident> findByBuilding_BuildingId(Integer buildingId);

    // used for login/duplicate-check since email is unique
    Optional<Resident> findByEmail(String email);

    // quick search by flat number within a building
    Optional<Resident> findByBuilding_BuildingIdAndFlatNo(Integer buildingId, String flatNo);
}