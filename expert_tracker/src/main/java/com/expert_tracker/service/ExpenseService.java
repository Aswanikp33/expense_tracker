package com.expert_tracker.service;

import com.expert_tracker.entity.Expense;
import com.expert_tracker.entity.User;
import com.expert_tracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }
    public List<Expense> getExpensesByMonthAndYear(User user, int year, int month) {
        return expenseRepository.findExpensesByMonthAndYear(user, year, month);
    }

    public double getTotalExpenseByMonthAndYear(User user, int year, int month) {
        Double total = expenseRepository.getTotalExpenseByMonthAndYear(user, year, month);
        return total != null ? total : 0.0;
    }

    public double getTotalExpenseByYear(User user, int year) {
        Double total = expenseRepository.getTotalExpenseByYear(user, year);
        return total != null ? total : 0.0;
    }
    public List<Expense> getUserExpensesByYear(User user, int year) {
        return expenseRepository.findExpensesByYear(user, year);
    }
    public List<Expense> getExpensesByCategory(User user, String category) {
        return expenseRepository.findExpensesByCategory(user, category);
    }
    public double getTotalExpenseByCategory(User user, String category) {
        Double total = expenseRepository.getTotalExpenseByCategory(user, category);
        return total != null ? total : 0.0;
    }


    public List<Expense> getUserExpenses(User user) {
        return expenseRepository.findByUser(user);
    }

    public void addExpense(Expense expense) {
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now());  // Default to today if date is not provided
        }
        expenseRepository.save(expense);
    }

    public Expense findExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    public Expense updateExpense(Long expenseId, Expense updatedExpense, String username) {
        Expense existingExpense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new RuntimeException("Expense not found"));
    
        if (!existingExpense.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized action");
        }
    
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setDescription(updatedExpense.getDescription());
    
        return expenseRepository.save(existingExpense);
    }
    
    public void deleteExpense(Long expenseId, User user) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getUsername().equals(user.getUsername())) {
            throw new RuntimeException("Unauthorized action");
        }

        expenseRepository.delete(expense);
    }
    public Map<String, Double> getTotalExpenseByCategory(User user) {
        List<Expense> expenses = expenseRepository.findByUser(user);
        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));
    }

    public Map<Integer, Double> getTotalExpenseByMonth(User user, int year) {
        List<Expense> expenses = expenseRepository.findByUserAndYear(user, year);
        return expenses.stream()
                .collect(Collectors.groupingBy(exp -> exp.getDate().getMonthValue(), Collectors.summingDouble(Expense::getAmount)));
    }

}
