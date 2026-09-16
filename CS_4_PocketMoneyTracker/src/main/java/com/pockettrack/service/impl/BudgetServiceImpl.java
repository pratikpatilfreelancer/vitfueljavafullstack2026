package com.pockettrack.service.impl;

import com.pockettrack.dao.MonthlyBudgetDao;
import com.pockettrack.exception.ResourceNotFoundException;
import com.pockettrack.model.MonthlyBudget;
import com.pockettrack.service.BudgetService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class BudgetServiceImpl implements BudgetService {
    private final MonthlyBudgetDao monthlyBudgetDao;

    public BudgetServiceImpl(MonthlyBudgetDao monthlyBudgetDao) {
        this.monthlyBudgetDao = monthlyBudgetDao;
    }

    @Override
    public MonthlyBudget setBudget(int userId, int month, int year, BigDecimal amount) {
        if (userId <= 0) throw new IllegalArgumentException("Invalid user id.");
        if (month < 1 || month > 12) throw new IllegalArgumentException("Month must be between 1 and 12.");
        if (year < 2000) throw new IllegalArgumentException("Invalid year.");
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Budget amount must be positive.");

        MonthlyBudget budget = monthlyBudgetDao.findByUserAndMonth(userId, month, year).orElse(null);
        if (budget == null) {
            budget = new MonthlyBudget();
            budget.setUserId(userId);
            budget.setMonth(month);
            budget.setYear(year);
            budget.setAmount(amount);
            return monthlyBudgetDao.save(budget);
        }

        budget.setAmount(amount);
        monthlyBudgetDao.update(budget);
        return budget;
    }

    @Override
    public MonthlyBudget getBudget(int userId, int month, int year) {
        return monthlyBudgetDao.findByUserAndMonth(userId, month, year)
            .orElseThrow(() -> new ResourceNotFoundException("No budget has been set for this month."));
    }
}
