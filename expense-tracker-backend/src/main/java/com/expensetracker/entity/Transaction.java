package com.expensetracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Transaction entity representing individual expenses or income
 * Each transaction belongs to a user and a category
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_category_id", columnList = "category_id"),
    @Index(name = "idx_transaction_date", columnList = "transaction_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(length = 500)
    private String description;
    
    @NotNull(message = "Transaction date is required")
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type = TransactionType.EXPENSE;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.EAGER)  // Changed to EAGER to avoid lazy loading issues
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"transactions", "budgets"})  // Ignore circular references
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)  // Changed to EAGER so category data is always loaded
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties({"transactions", "budgets"})  // Ignore circular references
    private Category category;
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Transaction type enumeration
     */
    public enum TransactionType {
        INCOME,
        EXPENSE
    }
}