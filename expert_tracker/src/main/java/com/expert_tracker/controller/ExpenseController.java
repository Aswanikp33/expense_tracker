package com.expert_tracker.controller;

import com.expert_tracker.entity.Expense;
import com.expert_tracker.entity.User;
import com.expert_tracker.service.ExpenseService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public String listExpenses(@AuthenticationPrincipal User user, Model model) {
        List<Expense> expenses = expenseService.getUserExpenses(user);
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        double monthlyTotal = expenseService.getTotalExpenseByMonthAndYear(user, currentYear, currentMonth);
        double yearlyTotal = expenseService.getTotalExpenseByYear(user, currentYear);

        model.addAttribute("expenses", expenses);
        model.addAttribute("monthlyTotal", monthlyTotal);
        model.addAttribute("yearlyTotal", yearlyTotal);
        return "expenses";
    }

    @GetMapping("/filter")
    public String filterExpensesByMonth(@AuthenticationPrincipal User user,
                                        @RequestParam(required = false) Integer year,
                                        @RequestParam(required = false) Integer month,
                                        Model model) {
        LocalDate today = LocalDate.now();
        year = (year != null) ? year : today.getYear();
        month = (month != null) ? month : today.getMonthValue();

        List<Expense> filteredExpenses = expenseService.getExpensesByMonthAndYear(user, year, month);
        double totalExpense = expenseService.getTotalExpenseByMonthAndYear(user, year, month);

        model.addAttribute("filteredExpenses", filteredExpenses);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);

        return "filtered-expenses";
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
        model.addAttribute("expense", expenseService.findExpenseById(id));
        return "edit-expense";
    }

    @PostMapping("/edit")
    public String editExpense(@RequestParam Long id, @ModelAttribute Expense expense, 
                          Principal principal, RedirectAttributes redirectAttributes) {
    try {
        String username = principal.getName();  // Get logged-in username
        expenseService.updateExpense(id, expense, username);
        redirectAttributes.addFlashAttribute("successMessage", "Expense updated successfully!");
    } catch (RuntimeException e) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    }
    return "redirect:/expenses";
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
}
