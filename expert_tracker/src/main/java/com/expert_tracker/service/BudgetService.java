package com.expert_tracker.service;

import com.expert_tracker.entity.Budget;
import com.expert_tracker.entity.User;
import com.expert_tracker.repository.BudgetRepository;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Budget getBudgetForUser(User user) {
        return budgetRepository.findByUser(user).orElse(null);
    }

    public void setBudget(User user, double monthlyBudget, double yearlyBudget) {
        Budget budget = budgetRepository.findByUser(user).orElse(new Budget());
        budget.setUser(user);
        budget.setMonthlyBudget(monthlyBudget);
        budget.setYearlyBudget(yearlyBudget);
        budgetRepository.save(budget);
    }
}
