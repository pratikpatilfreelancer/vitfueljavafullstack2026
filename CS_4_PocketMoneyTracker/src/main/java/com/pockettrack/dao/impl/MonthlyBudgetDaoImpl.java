package com.pockettrack.dao.impl;

import com.pockettrack.dao.MonthlyBudgetDao;
import com.pockettrack.model.MonthlyBudget;
import com.pockettrack.util.mapper.MonthlyBudgetRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class MonthlyBudgetDaoImpl implements MonthlyBudgetDao {
    private final JdbcTemplate jdbcTemplate;

    public MonthlyBudgetDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MonthlyBudget save(MonthlyBudget budget) {
        String sql = "INSERT INTO MonthlyBudget (user_id, month, year, amount) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, budget.getUserId());
            ps.setInt(2, budget.getMonth());
            ps.setInt(3, budget.getYear());
            ps.setBigDecimal(4, budget.getAmount());
            return ps;
        }, keyHolder);
        budget.setBudgetId(keyHolder.getKey().intValue());
        return findByUserAndMonth(budget.getUserId(), budget.getMonth(), budget.getYear()).orElse(budget);
    }

    @Override
    public Optional<MonthlyBudget> findByUserAndMonth(int userId, int month, int year) {
        String sql = "SELECT * FROM MonthlyBudget WHERE user_id = ? AND month = ? AND year = ?";
        return jdbcTemplate.query(sql, new MonthlyBudgetRowMapper(), userId, month, year).stream().findFirst();
    }

    @Override
    public List<MonthlyBudget> findByUser(int userId) {
        return jdbcTemplate.query(
            "SELECT * FROM MonthlyBudget WHERE user_id = ? ORDER BY year DESC, month DESC",
            new MonthlyBudgetRowMapper(), userId
        );
    }

    @Override
    public void update(MonthlyBudget budget) {
        jdbcTemplate.update(
            "UPDATE MonthlyBudget SET amount = ? WHERE budget_id = ?",
            budget.getAmount(), budget.getBudgetId()
        );
    }
}
