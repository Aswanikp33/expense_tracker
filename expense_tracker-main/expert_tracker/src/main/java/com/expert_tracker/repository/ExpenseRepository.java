package com.expert_tracker.repository;
import com.expert_tracker.entity.Expense;
import com.expert_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
    @Query("SELECT e FROM Expense e WHERE e.user = :user AND YEAR(e.date) = :year AND MONTH(e.date) = :month")
    List<Expense> findExpensesByMonthAndYear(@Param("user") User user, @Param("year") int year, @Param("month") int month);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND YEAR(e.date) = :year AND MONTH(e.date) = :month")
    Double getTotalExpenseByMonthAndYear(@Param("user") User user, @Param("year") int year, @Param("month") int month);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND YEAR(e.date) = :year")
    Double getTotalExpenseByYear(@Param("user") User user, @Param("year") int year);

    @Query("SELECT e FROM Expense e WHERE e.user = :user AND YEAR(e.date) = :year")
    List<Expense> findExpensesByYear(@Param("user") User user, @Param("year") int year);

    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.category = :category")
    List<Expense> findExpensesByCategory(@Param("user") User user, @Param("category") String category);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.category = :category")
    Double getTotalExpenseByCategory(@Param("user") User user, @Param("category") String category);


}