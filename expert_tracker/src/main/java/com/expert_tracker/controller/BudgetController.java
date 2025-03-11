package com.expert_tracker.controller;

import com.expert_tracker.entity.User;
import com.expert_tracker.service.BudgetService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/budget")  // ✅ Explicitly map this controller to `/budget`
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public String showBudgetPage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("budget", budgetService.getBudgetForUser(user));
        return "budget";  // ✅ Only show budget page when explicitly accessed at `/budget`
    }

    @PostMapping("/save")
    public String saveBudget(@AuthenticationPrincipal User user,
                             @RequestParam double monthlyBudget,
                             @RequestParam double yearlyBudget) {
        budgetService.setBudget(user, monthlyBudget, yearlyBudget);
        return "redirect:/expenses";
    }
}
