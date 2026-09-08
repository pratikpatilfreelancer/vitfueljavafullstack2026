package com.capgemini.apartment_maintenance.service.impl;

import com.capgemini.apartment_maintenance.dto.ComplaintCategoryRequestDTO;
import com.capgemini.apartment_maintenance.dto.ComplaintCategoryResponseDTO;
import com.capgemini.apartment_maintenance.entity.ComplaintCategory;
import com.capgemini.apartment_maintenance.exception.InvalidOperationException;
import com.capgemini.apartment_maintenance.exception.ResourceNotFoundException;
import com.capgemini.apartment_maintenance.repository.ComplaintCategoryRepository;
import com.capgemini.apartment_maintenance.service.ComplaintCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintCategoryServiceImpl implements ComplaintCategoryService {

    private final ComplaintCategoryRepository categoryRepository;

    @Override
    @Transactional
    public ComplaintCategoryResponseDTO createCategory(ComplaintCategoryRequestDTO dto) {
        categoryRepository.findByCategoryName(dto.categoryName()).ifPresent(c -> {
            throw new InvalidOperationException("Category '" + dto.categoryName() + "' already exists");
        });
        ComplaintCategory category = ComplaintCategory.builder()
                .categoryName(dto.categoryName())
                .build();
        ComplaintCategory saved = categoryRepository.save(category);
        log.info("Created category '{}'", saved.getCategoryName());
        return toResponseDTO(saved);
    }

    @Override
    public List<ComplaintCategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    @Override
    public ComplaintCategoryResponseDTO getCategoryById(Integer id) {
        return toResponseDTO(findCategoryOrThrow(id));
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        ComplaintCategory category = findCategoryOrThrow(id);
        if (!category.getComplaints().isEmpty()) {
            throw new InvalidOperationException(
                    "Cannot delete category id " + id + " — it is used by existing complaints.");
        }
        categoryRepository.delete(category);
        log.info("Deleted category id {}", id);
    }

    private ComplaintCategory findCategoryOrThrow(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private ComplaintCategoryResponseDTO toResponseDTO(ComplaintCategory c) {
        return new ComplaintCategoryResponseDTO(c.getCategoryId(), c.getCategoryName());
    }
}