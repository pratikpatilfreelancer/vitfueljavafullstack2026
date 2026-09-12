package com.capgemini.apartment_maintenance.service;

import com.capgemini.apartment_maintenance.dto.ComplaintCategoryRequestDTO;
import com.capgemini.apartment_maintenance.dto.ComplaintCategoryResponseDTO;
import java.util.List;

public interface ComplaintCategoryService {
    ComplaintCategoryResponseDTO createCategory(ComplaintCategoryRequestDTO dto);
    List<ComplaintCategoryResponseDTO> getAllCategories();
    ComplaintCategoryResponseDTO getCategoryById(Integer id);
    void deleteCategory(Integer id);
}