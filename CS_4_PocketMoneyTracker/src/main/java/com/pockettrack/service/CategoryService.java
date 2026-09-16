package com.pockettrack.service;

import com.pockettrack.model.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    List<Category> getCategoriesByType(String type);
}
