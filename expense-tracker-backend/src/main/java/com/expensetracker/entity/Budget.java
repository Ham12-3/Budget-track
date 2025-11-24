package com.expensetracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Budget entity for managing monthly spending limits per category
 * Users can set budgets for specific categories and time periods
 */
@Entity
@Table(name = "budgets", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "category_id", "month", "year"}),
       indexes = {
           @Index(name = "idx_budget_user", columnList = "user_id"),
           @Index(name = "idx_budget_period", columnList = "month, year")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Budget amount is required")
    @DecimalMin(value = "0.01", message = "Budget amount must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @NotNull(message = "Month is required")
    @Column(nullable = false)
    private Integer month; // 1-12
    
    @NotNull(message = "Year is required")
    @Column(nullable = false)
    private Integer year;
    
    @Column(name = "alert_threshold")
    private Integer alertThreshold = 80; // Alert when spending reaches this percentage
    
    @Column(length = 500)
    private String notes;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    /**
     * Calculate the start date of the budget period
     */
    @Transient
    public LocalDate getStartDate() {
        return LocalDate.of(year, month, 1);
    }
    
    /**
     * Calculate the end date of the budget period
     */
    @Transient
    public LocalDate getEndDate() {
        return getStartDate().plusMonths(1).minusDays(1);
    }
}