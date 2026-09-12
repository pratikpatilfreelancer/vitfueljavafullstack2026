package com.capgemini.apartment_maintenance.repository;

import com.capgemini.apartment_maintenance.entity.ComplaintCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for ComplaintCategory entity.
 */
public interface ComplaintCategoryRepository extends JpaRepository<ComplaintCategory, Integer> {

    Optional<ComplaintCategory> findByCategoryName(String categoryName);
}