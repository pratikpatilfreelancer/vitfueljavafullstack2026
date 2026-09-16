package com.pockettrack.service;

import com.pockettrack.model.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    Transaction addTransaction(Transaction txn);
    BigDecimal getTotalSpent(int userId, int month, int year);
    BigDecimal getExtraIncome(int userId, int month, int year);
    BigDecimal getRemainingBalance(int userId, int month, int year);
    List<Transaction> getDailyBreakdown(int userId, int month, int year);
}
