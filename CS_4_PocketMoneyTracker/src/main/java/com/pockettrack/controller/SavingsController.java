package com.pockettrack.controller;

import com.pockettrack.model.Savings;
import com.pockettrack.service.SavingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
@RequestMapping("/savings")
public class SavingsController {
    private static final int CURRENT_USER_ID = 1;

    private final SavingsService savingsService;

    public SavingsController(SavingsService savingsService) {
        this.savingsService = savingsService;
    }

    @GetMapping
    public String viewSavings(Model model) {
        Savings savings = savingsService.getSavings(CURRENT_USER_ID);
        model.addAttribute("savings", savings);
        model.addAttribute("history", savingsService.getSavingsHistory(CURRENT_USER_ID));
        return "savings";
    }

    @PostMapping("/deposit")
    public String transferToSavings(
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String note) {
        savingsService.depositToSavings(CURRENT_USER_ID, amount, note);
        return "redirect:/savings";
    }
}
