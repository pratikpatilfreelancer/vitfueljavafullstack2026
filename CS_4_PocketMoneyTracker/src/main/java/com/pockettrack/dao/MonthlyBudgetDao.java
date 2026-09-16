package com.pockettrack.dao;

import com.pockettrack.model.MonthlyBudget;
import java.util.List;
import java.util.Optional;

public interface MonthlyBudgetDao {
    MonthlyBudget save(MonthlyBudget budget);
    Optional<MonthlyBudget> findByUserAndMonth(int userId, int month, int year);
    List<MonthlyBudget> findByUser(int userId);
    void update(MonthlyBudget budget);
}
