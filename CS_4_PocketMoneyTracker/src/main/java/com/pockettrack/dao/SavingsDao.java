package com.pockettrack.dao;

import com.pockettrack.model.Savings;
import java.math.BigDecimal;
import java.util.Optional;

public interface SavingsDao {
    Savings save(Savings savings);
    Optional<Savings> findByUserId(int userId);
    void updateBalance(int userId, BigDecimal newBalance);
}
