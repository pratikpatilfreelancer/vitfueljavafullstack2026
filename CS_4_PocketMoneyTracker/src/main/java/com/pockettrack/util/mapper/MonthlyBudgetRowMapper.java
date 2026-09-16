package com.pockettrack.util.mapper;

import com.pockettrack.model.MonthlyBudget;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MonthlyBudgetRowMapper implements RowMapper<MonthlyBudget> {
    @Override
    public MonthlyBudget mapRow(ResultSet rs, int rowNum) throws SQLException {
        MonthlyBudget budget = new MonthlyBudget();
        budget.setBudgetId(rs.getInt("budget_id"));
        budget.setUserId(rs.getInt("user_id"));
        budget.setMonth(rs.getInt("month"));
        budget.setYear(rs.getInt("year"));
        budget.setAmount(rs.getBigDecimal("amount"));
        return budget;
    }
}
