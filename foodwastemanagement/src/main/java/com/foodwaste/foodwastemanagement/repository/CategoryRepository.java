package com.foodwaste.foodwastemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodwaste.foodwastemanagement.entity.Category;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

}