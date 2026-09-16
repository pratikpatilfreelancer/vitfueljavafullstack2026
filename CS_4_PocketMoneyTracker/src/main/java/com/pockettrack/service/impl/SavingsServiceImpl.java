package com.pockettrack.service.impl;

import com.pockettrack.dao.SavingsDao;
import com.pockettrack.dao.SavingsLogDao;
import com.pockettrack.exception.InsufficientSavingsException;
import com.pockettrack.exception.ResourceNotFoundException;
import com.pockettrack.model.Savings;
import com.pockettrack.model.SavingsLog;
import com.pockettrack.service.SavingsService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class SavingsServiceImpl implements SavingsService {
    private final SavingsDao savingsDao;
    private final SavingsLogDao savingsLogDao;

    public SavingsServiceImpl(SavingsDao savingsDao, SavingsLogDao savingsLogDao) {
        this.savingsDao = savingsDao;
        this.savingsLogDao = savingsLogDao;
    }

    @Override
    public Savings getSavings(int userId) {
        return savingsDao.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Savings record not found."));
    }

    @Override
    public SavingsLog depositToSavings(int userId, BigDecimal amount, String note) {
        validateAmount(amount);
        Savings savings = getSavings(userId);
        BigDecimal newBalance = savings.getBalance().add(amount);
        savingsDao.updateBalance(userId, newBalance);

        SavingsLog log = new SavingsLog();
        log.setSavingsId(savings.getSavingsId());
        log.setAmount(amount);
        log.setLogType("DEPOSIT");
        log.setNote(note == null || note.isBlank() ? "Manual deposit" : note);
        return savingsLogDao.save(log);
    }

    @Override
    public SavingsLog autoDeductForOverspend(int userId, BigDecimal shortfall) {
        validateAmount(shortfall);
        Savings savings = getSavings(userId);

        if (savings.getBalance().compareTo(shortfall) < 0) {
            throw new InsufficientSavingsException(
                "Insufficient savings to cover the overspend. Available: " +
                savings.getBalance() + ", Required: " + shortfall
            );
        }

        BigDecimal newBalance = savings.getBalance().subtract(shortfall);
        savingsDao.updateBalance(userId, newBalance);

        SavingsLog log = new SavingsLog();
        log.setSavingsId(savings.getSavingsId());
        log.setAmount(shortfall);
        log.setLogType("AUTO_DEDUCT");
        log.setNote("Auto-deducted for overspend.");
        return savingsLogDao.save(log);
    }

    @Override
    public List<SavingsLog> getSavingsHistory(int userId) {
        Savings savings = getSavings(userId);
        return savingsLogDao.findBySavingsId(savings.getSavingsId());
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
    }
}
