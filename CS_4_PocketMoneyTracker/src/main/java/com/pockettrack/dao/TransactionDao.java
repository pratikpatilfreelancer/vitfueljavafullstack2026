package com.pockettrack.dao;

import com.pockettrack.model.Transaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionDao {
    Transaction save(Transaction txn);
    Optional<Transaction> findById(int transactionId);
    List<Transaction> findByUserAndMonth(int userId, int month, int year);
    BigDecimal sumByUserMonthAndType(int userId, int month, int year, String type);
    List<Transaction> findDailyBreakdown(int userId, int month, int year);
    void deleteById(int transactionId);
}
