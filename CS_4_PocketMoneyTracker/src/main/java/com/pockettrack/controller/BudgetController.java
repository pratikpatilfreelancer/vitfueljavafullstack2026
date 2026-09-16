package com.pockettrack.controller;

import com.pockettrack.model.MonthlyBudget;
import com.pockettrack.service.BudgetService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.time.LocalDate;

@Controller
@RequestMapping("/budget")
public class BudgetController {
    private static final int CURRENT_USER_ID = 1;

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping("/set")
    public String setBudget(@ModelAttribute MonthlyBudget budget) {
        LocalDate today = LocalDate.now();

        int month = budget.getMonth() > 0 ? budget.getMonth() : today.getMonthValue();
        int year = budget.getYear() > 0 ? budget.getYear() : today.getYear();

        budgetService.setBudget(CURRENT_USER_ID, month, year, budget.getAmount());
        return "redirect:/dashboard";
    }
}
