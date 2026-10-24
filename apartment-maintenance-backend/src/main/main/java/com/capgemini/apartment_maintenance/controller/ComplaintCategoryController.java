package com.capgemini.apartment_maintenance.controller;

import com.capgemini.apartment_maintenance.dto.ComplaintCategoryRequestDTO;
import com.capgemini.apartment_maintenance.dto.ComplaintCategoryResponseDTO;
import com.capgemini.apartment_maintenance.service.ComplaintCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ComplaintCategoryController {

    private final ComplaintCategoryService categoryService;

    @PostMapping
    public ResponseEntity<ComplaintCategoryResponseDTO> createCategory(
            @Valid @RequestBody ComplaintCategoryRequestDTO dto) {
        ComplaintCategoryResponseDTO created = categoryService.createCategory(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ComplaintCategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintCategoryResponseDTO> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // no PUT — service has no update method for categories; delete + recreate if a rename is needed

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}