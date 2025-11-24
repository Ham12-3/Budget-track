package com.expensetracker.service;

import com.expensetracker.entity.Budget;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction.TransactionType;
import com.expensetracker.entity.User;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.BudgetRepository.BudgetWithSpent;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service layer for Budget-related business logic
 * Handles budget creation, tracking, and alerts
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    
    /**
     * Create or update a budget
     */
    public Budget createOrUpdateBudget(Budget budget, Long userId, Long categoryId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
        
        // Check if budget already exists for this user, category, and period
        Optional<Budget> existingBudget = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
            userId, categoryId, budget.getMonth(), budget.getYear()
        );
        
        if (existingBudget.isPresent()) {
            // Update existing budget
            Budget budgetToUpdate = existingBudget.get();
            budgetToUpdate.setAmount(budget.getAmount());
            budgetToUpdate.setAlertThreshold(budget.getAlertThreshold());
            budgetToUpdate.setNotes(budget.getNotes());
            return budgetRepository.save(budgetToUpdate);
        } else {
            // Create new budget
            budget.setUser(user);
            budget.setCategory(category);
            return budgetRepository.save(budget);
        }
    }
    
    /**
     * Get budget by ID
     */
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }
    
    /**
     * Get all budgets for a user
     */
    public List<Budget> getUserBudgets(Long userId) {
        return budgetRepository.findByUserId(userId);
    }
    
    /**
     * Get budgets for a specific month with spending status
     */
    public List<Map<String, Object>> getMonthlyBudgetsWithStatus(Long userId, int month, int year) {
        List<BudgetWithSpent> budgetsWithSpent = budgetRepository.findBudgetsWithSpent(userId, month, year);
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (BudgetWithSpent bws : budgetsWithSpent) {
            Map<String, Object> budgetStatus = new HashMap<>();
            Budget budget = bws.getBudget();
            BigDecimal spent = bws.getSpent();
            BigDecimal percentage = BigDecimal.ZERO;
            
            if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                percentage = spent.multiply(BigDecimal.valueOf(100))
                    .divide(budget.getAmount(), 2, RoundingMode.HALF_UP);
            }
            
            budgetStatus.put("budget", budget);
            budgetStatus.put("spent", spent);
            budgetStatus.put("remaining", budget.getAmount().subtract(spent));
            budgetStatus.put("percentage", percentage);
            budgetStatus.put("isOverBudget", spent.compareTo(budget.getAmount()) > 0);
            budgetStatus.put("isNearLimit", percentage.compareTo(BigDecimal.valueOf(budget.getAlertThreshold())) >= 0);
            
            results.add(budgetStatus);
        }
        
        return results;
    }
    
    /**
     * Get budget status for a specific category
     */
    public Map<String, Object> getBudgetStatus(Long userId, Long categoryId, int month, int year) {
        Optional<Budget> budgetOpt = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
            userId, categoryId, month, year
        );
        
        if (budgetOpt.isEmpty()) {
            return null;
        }
        
        Budget budget = budgetOpt.get();
        LocalDate startDate = budget.getStartDate();
        LocalDate endDate = budget.getEndDate();
        
        // Calculate spent amount
        BigDecimal spent = transactionRepository.calculateTotalByCategoryAndDateRange(
            userId, categoryId, TransactionType.EXPENSE, startDate, endDate
        );
        
        BigDecimal percentage = BigDecimal.ZERO;
        if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            percentage = spent.multiply(BigDecimal.valueOf(100))
                .divide(budget.getAmount(), 2, RoundingMode.HALF_UP);
        }
        
        Map<String, Object> status = new HashMap<>();
        status.put("budget", budget);
        status.put("spent", spent);
        status.put("remaining", budget.getAmount().subtract(spent));
        status.put("percentage", percentage);
        status.put("isOverBudget", spent.compareTo(budget.getAmount()) > 0);
        status.put("isNearLimit", percentage.compareTo(BigDecimal.valueOf(budget.getAlertThreshold())) >= 0);
        
        return status;
    }
    
    /**
     * Delete budget
     */
    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new IllegalArgumentException("Budget not found with id: " + id);
        }
        budgetRepository.deleteById(id);
    }
    
    /**
     * Get budget alerts for user
     */
    public List<Map<String, Object>> getBudgetAlerts(Long userId) {
        LocalDate now = LocalDate.now();
        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(
            userId, now.getMonthValue(), now.getYear()
        );
        
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        for (Budget budget : budgets) {
            BigDecimal spent = transactionRepository.calculateTotalByCategoryAndDateRange(
                userId, budget.getCategory().getId(), TransactionType.EXPENSE,
                budget.getStartDate(), budget.getEndDate()
            );
            
            BigDecimal percentage = BigDecimal.ZERO;
            if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                percentage = spent.multiply(BigDecimal.valueOf(100))
                    .divide(budget.getAmount(), 2, RoundingMode.HALF_UP);
            }
            
            // Check if alert should be triggered
            if (percentage.compareTo(BigDecimal.valueOf(budget.getAlertThreshold())) >= 0) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("category", budget.getCategory().getName());
                alert.put("budgetAmount", budget.getAmount());
                alert.put("spent", spent);
                alert.put("percentage", percentage);
                alert.put("message", String.format("You've spent %.0f%% of your %s budget", 
                    percentage, budget.getCategory().getName()));
                alert.put("severity", percentage.compareTo(BigDecimal.valueOf(100)) >= 0 ? "HIGH" : "MEDIUM");
                
                alerts.add(alert);
            }
        }
        
        return alerts;
    }
}