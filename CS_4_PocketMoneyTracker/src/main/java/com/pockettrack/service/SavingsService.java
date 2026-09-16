package com.pockettrack.service;

import com.pockettrack.model.Savings;
import com.pockettrack.model.SavingsLog;
import java.math.BigDecimal;
import java.util.List;

public interface SavingsService {
    Savings getSavings(int userId);
    SavingsLog depositToSavings(int userId, BigDecimal amount, String note);
    SavingsLog autoDeductForOverspend(int userId, BigDecimal shortfall);
    List<SavingsLog> getSavingsHistory(int userId);
}
