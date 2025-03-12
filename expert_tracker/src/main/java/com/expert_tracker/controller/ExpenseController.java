package com.expert_tracker.controller;

import com.expert_tracker.entity.Budget;
import com.expert_tracker.entity.Expense;
import com.expert_tracker.entity.User;
import com.expert_tracker.service.BudgetService;
import com.expert_tracker.service.ExpenseService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final BudgetService budgetService;

    public ExpenseController(ExpenseService expenseService, BudgetService budgetService) {
        this.expenseService = expenseService;
        this.budgetService = budgetService;
    }
    @GetMapping
    public String listExpenses(@AuthenticationPrincipal User user, Model model) {
        List<Expense> expenses = expenseService.getUserExpenses(user);
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        double monthlyTotal = expenseService.getTotalExpenseByMonthAndYear(user, currentYear, currentMonth);
        double yearlyTotal = expenseService.getTotalExpenseByYear(user, currentYear);

        Budget budget = budgetService.getBudgetForUser(user);
        double monthlyBudget = budget != null ? budget.getMonthlyBudget() : 0.0;
        double yearlyBudget = budget != null ? budget.getYearlyBudget() : 0.0;

        // ✅ Show warning only when expenses exceed the budget (not at 80%)
        boolean monthlyWarning = monthlyBudget > 0 && monthlyTotal >= monthlyBudget;
        boolean yearlyWarning = yearlyBudget > 0 && yearlyTotal >= yearlyBudget;

        model.addAttribute("expenses", expenses);
        model.addAttribute("monthlyTotal", monthlyTotal);
        model.addAttribute("yearlyTotal", yearlyTotal);
        model.addAttribute("monthlyBudget", monthlyBudget);
        model.addAttribute("yearlyBudget", yearlyBudget);
        model.addAttribute("monthlyWarning", monthlyWarning);
        model.addAttribute("yearlyWarning", yearlyWarning);

        return "expenses";
    }

    @GetMapping("/filter")
    public String filterExpenses(@AuthenticationPrincipal User user,
                                 @RequestParam(required = false) Integer year,
                                 @RequestParam(required = false) Integer month,
                                 @RequestParam(required = false) String category,
                                 Model model) {
        List<Expense> filteredExpenses = new ArrayList<>();
        double totalExpense = 0.0;

        // Ensure current monthly and yearly totals are always available
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        double currentMonthlyTotal = expenseService.getTotalExpenseByMonthAndYear(user, currentYear, currentMonth);
        double currentYearlyTotal = expenseService.getTotalExpenseByYear(user, currentYear);

        // Set default values for filtered totals
        double filteredMonthlyTotal = 0.0;
        double filteredYearlyTotal = 0.0;

        if (year != null && month != null && category != null && !category.isEmpty()) {
            filteredExpenses = expenseService.getExpensesByMonthYearAndCategory(user, year, month, category);
            totalExpense = expenseService.getTotalExpenseByMonthYearAndCategory(user, year, month, category);
            filteredMonthlyTotal = totalExpense;
            filteredYearlyTotal = expenseService.getTotalExpenseByYear(user, year);
        } else if (year != null && month != null) {
            filteredExpenses = expenseService.getExpensesByMonthAndYear(user, year, month);
            totalExpense = expenseService.getTotalExpenseByMonthAndYear(user, year, month);
            filteredMonthlyTotal = totalExpense;
            filteredYearlyTotal = expenseService.getTotalExpenseByYear(user, year);
        } else if (year != null) {
            filteredExpenses = expenseService.getUserExpensesByYear(user, year);
            totalExpense = expenseService.getTotalExpenseByYear(user, year);
            filteredYearlyTotal = totalExpense;
            filteredMonthlyTotal = expenseService.getTotalExpenseByMonthAndYear(user, year, currentMonth);
        } else if (category != null && !category.isEmpty()) {
            filteredExpenses = expenseService.getExpensesByCategory(user, category);
            totalExpense = expenseService.getTotalExpenseByCategory(user, category);
            filteredYearlyTotal = expenseService.getTotalExpenseByYear(user, currentYear);
            filteredMonthlyTotal = expenseService.getTotalExpenseByMonthAndYear(user, currentYear, currentMonth);
        } else {
            filteredExpenses = expenseService.getUserExpenses(user);
        }

        // Ensure the budget is included in the filtering
        Budget budget = budgetService.getBudgetForUser(user);
        double monthlyBudget = budget != null ? budget.getMonthlyBudget() : 0.0;
        double yearlyBudget = budget != null ? budget.getYearlyBudget() : 0.0;

        // ✅ Correctly calculate warnings
        // Ensure warning calculations are based on the correct values
        boolean monthlyWarning = monthlyBudget > 0 && (filteredMonthlyTotal > 0 ? filteredMonthlyTotal : currentMonthlyTotal) >= monthlyBudget;
        boolean yearlyWarning = yearlyBudget > 0 && (filteredYearlyTotal > 0 ? filteredYearlyTotal : currentYearlyTotal) >= yearlyBudget;


        model.addAttribute("filteredExpenses", filteredExpenses);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("monthlyTotal", filteredMonthlyTotal > 0 ? filteredMonthlyTotal : currentMonthlyTotal);
        model.addAttribute("yearlyTotal", filteredYearlyTotal > 0 ? filteredYearlyTotal : currentYearlyTotal);
        model.addAttribute("monthlyBudget", monthlyBudget);
        model.addAttribute("yearlyBudget", yearlyBudget);
        model.addAttribute("monthlyWarning", monthlyWarning);
        model.addAttribute("yearlyWarning", yearlyWarning);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedCategory", category);

        return "expenses";
    }

    @GetMapping("/add")
    public String showAddExpenseForm() {
        return "add-expense";
    }

    @PostMapping("/add")
    public String addExpense(@RequestParam String description,
                             @RequestParam double amount,
                             @RequestParam LocalDate date,
                             @RequestParam String category,  // New parameter
                             @AuthenticationPrincipal User user) {
        Expense expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setDate(date);
        expense.setUser(user);
        expense.setCategory(category);  // Set category
        expenseService.addExpense(expense);
        return "redirect:/expenses";
    }
    @GetMapping("/edit")
    public String showEditExpenseForm(@RequestParam Long id, Model model) {
        Expense expense = expenseService.findExpenseById(id);
        model.addAttribute("expense", expense);
        return "edit-expense";  // Make sure this file exists in your templates folder
    }

    @PostMapping("/edit")
    public String editExpense(@RequestParam Long id, @ModelAttribute Expense expense,
                              @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        try {
            expenseService.updateExpense(id, expense, user.getUsername());

            // ✅ Refresh budget calculations after update
            LocalDate today = LocalDate.now();
            int currentYear = today.getYear();
            int currentMonth = today.getMonthValue();

            double monthlyTotal = expenseService.getTotalExpenseByMonthAndYear(user, currentYear, currentMonth);
            double yearlyTotal = expenseService.getTotalExpenseByYear(user, currentYear);

            Budget budget = budgetService.getBudgetForUser(user);
            double monthlyBudget = budget != null ? budget.getMonthlyBudget() : 0.0;
            double yearlyBudget = budget != null ? budget.getYearlyBudget() : 0.0;

            boolean monthlyWarning = monthlyBudget > 0 && monthlyTotal >= (monthlyBudget * 0.8);
            boolean yearlyWarning = yearlyBudget > 0 && yearlyTotal >= (yearlyBudget * 0.8);

            // ✅ Update values before redirecting
            redirectAttributes.addFlashAttribute("monthlyTotal", monthlyTotal);
            redirectAttributes.addFlashAttribute("yearlyTotal", yearlyTotal);
            redirectAttributes.addFlashAttribute("monthlyWarning", monthlyWarning);
            redirectAttributes.addFlashAttribute("yearlyWarning", yearlyWarning);

            redirectAttributes.addFlashAttribute("successMessage", "Expense updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/expenses"; // ✅ Now the totals & warnings will be updated properly
    }


    @GetMapping("/delete")
    public String deleteExpense(@RequestParam Long id, @AuthenticationPrincipal User user, 
                                RedirectAttributes redirectAttributes) {
        try {
            expenseService.deleteExpense(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Expense deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/expenses";
    }
    @GetMapping("/chart-data")
    @ResponseBody
    public Map<String, Object> getExpenseChartData(@AuthenticationPrincipal User user) {
        Map<String, Object> chartData = new HashMap<>();

        // Get category-wise total expenses
        Map<String, Double> categoryExpenses = expenseService.getTotalExpenseByCategory(user);
        chartData.put("categoryLabels", categoryExpenses.keySet());
        chartData.put("categoryData", categoryExpenses.values());

        // Get monthly expenses for the current year
        int currentYear = LocalDate.now().getYear();
        Map<Integer, Double> monthlyExpenses = expenseService.getTotalExpenseByMonth(user, currentYear);
        chartData.put("monthLabels", monthlyExpenses.keySet());
        chartData.put("monthData", monthlyExpenses.values());

        // Get individual expenses (Descriptions + Amounts)
        List<Expense> expenses = expenseService.getUserExpenses(user);
        List<String> expenseDescriptions = new ArrayList<>();
        List<Double> expenseAmounts = new ArrayList<>();

        for (Expense expense : expenses) {
            expenseDescriptions.add(expense.getDescription());
            expenseAmounts.add(expense.getAmount());
        }

        chartData.put("expenseLabels", expenseDescriptions);
        chartData.put("expenseData", expenseAmounts);

        return chartData;
    }

    @GetMapping("/analyzer")
    public String showExpenseAnalyzer() {
        return "expense-analyzer";
    }
}
