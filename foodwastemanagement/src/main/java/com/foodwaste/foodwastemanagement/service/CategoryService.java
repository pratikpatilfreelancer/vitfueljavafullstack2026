package com.foodwaste.foodwastemanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foodwaste.foodwastemanagement.entity.Category;
import com.foodwaste.foodwastemanagement.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Create category
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}