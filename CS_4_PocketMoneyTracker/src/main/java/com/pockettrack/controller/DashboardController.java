package com.pockettrack.controller;

import com.pockettrack.model.Savings;
import com.pockettrack.service.BudgetService;
import com.pockettrack.service.SavingsService;
import com.pockettrack.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class DashboardController {
    private static final int CURRENT_USER_ID = 1;

    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final SavingsService savingsService;

    public DashboardController(
            TransactionService transactionService,
            BudgetService budgetService,
            SavingsService savingsService) {
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.savingsService = savingsService;
    }

    @GetMapping({"/", "/dashboard"})
    public String home(Model model) {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();

        BigDecimal totalSpent = transactionService.getTotalSpent(CURRENT_USER_ID, month, year);
        BigDecimal extraIncome = transactionService.getExtraIncome(CURRENT_USER_ID, month, year);
        BigDecimal remaining = transactionService.getRemainingBalance(CURRENT_USER_ID, month, year);
        Savings savings = savingsService.getSavings(CURRENT_USER_ID);

        model.addAttribute("budget", budgetService.getBudget(CURRENT_USER_ID, month, year));
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("extraIncome", extraIncome);
        model.addAttribute("remainingBalance", remaining);
        model.addAttribute("savingsBalance", savings.getBalance());
        model.addAttribute("transactions",
                transactionService.getDailyBreakdown(CURRENT_USER_ID, month, year));
        model.addAttribute("month", month);
        model.addAttribute("year", year);

        return "dashboard";
    }
}
