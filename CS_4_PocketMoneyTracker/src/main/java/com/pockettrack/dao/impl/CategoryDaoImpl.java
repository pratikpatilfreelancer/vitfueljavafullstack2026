package com.pockettrack.dao.impl;

import com.pockettrack.dao.CategoryDao;
import com.pockettrack.model.Category;
import com.pockettrack.util.mapper.CategoryRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDaoImpl implements CategoryDao {
    private final JdbcTemplate jdbcTemplate;

    public CategoryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Category save(Category category) {
        String sql = "INSERT INTO Category (name, type) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getName());
            ps.setString(2, category.getType());
            return ps;
        }, keyHolder);
        category.setCategoryId(keyHolder.getKey().intValue());
        return category;
    }

    @Override
    public Optional<Category> findById(int categoryId) {
        return jdbcTemplate.query(
            "SELECT * FROM Category WHERE category_id = ?",
            new CategoryRowMapper(), categoryId
        ).stream().findFirst();
    }

    @Override
    public List<Category> findAll() {
        return jdbcTemplate.query("SELECT * FROM Category ORDER BY name", new CategoryRowMapper());
    }

    @Override
    public List<Category> findByType(String type) {
        return jdbcTemplate.query(
            "SELECT * FROM Category WHERE type = ? ORDER BY name",
            new CategoryRowMapper(), type
        );
    }
}
