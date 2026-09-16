package com.pockettrack.service;

import com.pockettrack.model.MonthlyBudget;
import java.math.BigDecimal;

public interface BudgetService {
    MonthlyBudget setBudget(int userId, int month, int year, BigDecimal amount);
    MonthlyBudget getBudget(int userId, int month, int year);
}
