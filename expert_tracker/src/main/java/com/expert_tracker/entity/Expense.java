package com.expert_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter
@Setter
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDate date = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private String category;
}
