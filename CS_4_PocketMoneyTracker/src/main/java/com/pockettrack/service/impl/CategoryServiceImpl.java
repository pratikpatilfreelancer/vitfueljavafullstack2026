package com.pockettrack.service.impl;

import com.pockettrack.dao.CategoryDao;
import com.pockettrack.model.Category;
import com.pockettrack.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    @Override
    public List<Category> getCategoriesByType(String type) {
        if (!"EXPENSE".equalsIgnoreCase(type) && !"INCOME".equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("Category type must be EXPENSE or INCOME.");
        }
        return categoryDao.findByType(type.toUpperCase());
    }
}
