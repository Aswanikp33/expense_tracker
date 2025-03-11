package com.expert_tracker.repository;

import com.expert_tracker.entity.Budget;
import com.expert_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByUser(User user);
}
