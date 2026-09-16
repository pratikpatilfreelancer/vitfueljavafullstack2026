package com.pockettrack.service.impl;

import com.pockettrack.dao.CategoryDao;
import com.pockettrack.dao.TransactionDao;
import com.pockettrack.exception.ResourceNotFoundException;
import com.pockettrack.model.Category;
import com.pockettrack.model.MonthlyBudget;
import com.pockettrack.model.Transaction;
import com.pockettrack.service.BudgetService;
import com.pockettrack.service.SavingsService;
import com.pockettrack.service.TransactionService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionDao transactionDao;
    private final BudgetService budgetService;
    private final SavingsService savingsService;
    private final CategoryDao categoryDao;

    public TransactionServiceImpl(
            TransactionDao transactionDao,
            BudgetService budgetService,
            SavingsService savingsService,
            CategoryDao categoryDao) {
        this.transactionDao = transactionDao;
        this.budgetService = budgetService;
        this.savingsService = savingsService;
        this.categoryDao = categoryDao;
    }

    @Override
    public Transaction addTransaction(Transaction txn) {
        if (txn == null) throw new IllegalArgumentException("Transaction is required.");
        if (txn.getUserId() <= 0) throw new IllegalArgumentException("Invalid user id.");
        if (txn.getCategoryId() <= 0) throw new IllegalArgumentException("Category is required.");
        if (txn.getAmount() == null || txn.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive.");
        }
        if (txn.getTransactionDate() == null) {
            txn.setTransactionDate(LocalDate.now());
        }
        if (!"EXPENSE".equalsIgnoreCase(txn.getType()) && !"INCOME".equalsIgnoreCase(txn.getType())) {
            throw new IllegalArgumentException("Transaction type must be EXPENSE or INCOME.");
        }

        txn.setType(txn.getType().toUpperCase());

        Category category = categoryDao.findById(txn.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found."));

        if (!txn.getType().equalsIgnoreCase(category.getType())) {
            throw new IllegalArgumentException("Transaction type does not match the selected category.");
        }

        Transaction saved = transactionDao.save(txn);

        if ("EXPENSE".equals(saved.getType())) {
            BigDecimal totalSpent = getTotalSpent(saved.getUserId(),
                    saved.getTransactionDate().getMonthValue(),
                    saved.getTransactionDate().getYear());

            MonthlyBudget budget = budgetService.getBudget(
                    saved.getUserId(),
                    saved.getTransactionDate().getMonthValue(),
                    saved.getTransactionDate().getYear());

            if (totalSpent.compareTo(budget.getAmount()) > 0) {
                BigDecimal shortfall = totalSpent.subtract(budget.getAmount());
                savingsService.autoDeductForOverspend(saved.getUserId(), shortfall);
            }
        }

        return saved;
    }

    @Override
    public BigDecimal getTotalSpent(int userId, int month, int year) {
        return transactionDao.sumByUserMonthAndType(userId, month, year, "EXPENSE");
    }

    @Override
    public BigDecimal getExtraIncome(int userId, int month, int year) {
        return transactionDao.sumByUserMonthAndType(userId, month, year, "INCOME");
    }

    @Override
    public BigDecimal getRemainingBalance(int userId, int month, int year) {
        MonthlyBudget budget = budgetService.getBudget(userId, month, year);
        return budget.getAmount()
            .add(getExtraIncome(userId, month, year))
            .subtract(getTotalSpent(userId, month, year));
    }

    @Override
    public List<Transaction> getDailyBreakdown(int userId, int month, int year) {
        return transactionDao.findDailyBreakdown(userId, month, year);
    }
}
