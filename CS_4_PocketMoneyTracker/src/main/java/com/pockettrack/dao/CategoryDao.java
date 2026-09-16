package com.pockettrack.dao;

import com.pockettrack.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    Category save(Category category);
    Optional<Category> findById(int categoryId);
    List<Category> findAll();
    List<Category> findByType(String type);
}
