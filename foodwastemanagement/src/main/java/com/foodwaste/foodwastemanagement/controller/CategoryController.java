package com.foodwaste.foodwastemanagement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodwaste.foodwastemanagement.entity.Category;
import com.foodwaste.foodwastemanagement.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // CREATE
    @PostMapping
    public Category createCategory(
            @RequestBody Category category) {

        return categoryService.createCategory(category);
    }

    // READ
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }
}