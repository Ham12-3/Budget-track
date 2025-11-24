package com.expensetracker.controller;

import com.expensetracker.dto.BudgetDTO;
import com.expensetracker.entity.Budget;
import com.expensetracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for Budget management
 * Handles budget creation, tracking, and alerts
 */
@RestController
@RequestMapping("/users/{userId}/budgets")
@RequiredArgsConstructor
public class BudgetController {
    
    private final BudgetService budgetService;
    
    /**
     * Get all budgets for a user
     */
    @GetMapping
    public ResponseEntity<List<Budget>> getUserBudgets(@PathVariable Long userId) {
        List<Budget> budgets = budgetService.getUserBudgets(userId);
        return ResponseEntity.ok(budgets);
    }
    
    /**
     * Get budget by ID
     */
    @GetMapping("/{budgetId}")
    public ResponseEntity<Budget> getBudgetById(
            @PathVariable Long userId,
            @PathVariable Long budgetId) {
        return budgetService.getBudgetById(budgetId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get monthly budgets with spending status
     */
    @GetMapping("/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyBudgets(
            @PathVariable Long userId,
            @RequestParam int month,
            @RequestParam int year) {
        List<Map<String, Object>> budgets = budgetService.getMonthlyBudgetsWithStatus(
            userId, month, year);
        return ResponseEntity.ok(budgets);
    }
    
    /**
     * Get budget status for a specific category
     */
    @GetMapping("/category/{categoryId}/status")
    public ResponseEntity<Map<String, Object>> getBudgetStatus(
            @PathVariable Long userId,
            @PathVariable Long categoryId,
            @RequestParam int month,
            @RequestParam int year) {
        
        Map<String, Object> status = budgetService.getBudgetStatus(
            userId, categoryId, month, year);
        
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * Create or update budget
     */
    @PostMapping
    public ResponseEntity<Budget> createOrUpdateBudget(
            @PathVariable Long userId,
            @Valid @RequestBody BudgetDTO budgetDTO) {
        
        Budget budget = new Budget();
        budget.setAmount(budgetDTO.getAmount());
        budget.setMonth(budgetDTO.getMonth());
        budget.setYear(budgetDTO.getYear());
        budget.setAlertThreshold(budgetDTO.getAlertThreshold());
        budget.setNotes(budgetDTO.getNotes());
        
        Budget savedBudget = budgetService.createOrUpdateBudget(
            budget, userId, budgetDTO.getCategoryId());
        
        return new ResponseEntity<>(savedBudget, HttpStatus.CREATED);
    }
    
    /**
     * Update budget
     */
    @PutMapping("/{budgetId}")
    public ResponseEntity<Budget> updateBudget(
            @PathVariable Long userId,
            @PathVariable Long budgetId,
            @Valid @RequestBody BudgetDTO budgetDTO) {
        
        Budget budget = new Budget();
        budget.setId(budgetId);
        budget.setAmount(budgetDTO.getAmount());
        budget.setMonth(budgetDTO.getMonth());
        budget.setYear(budgetDTO.getYear());
        budget.setAlertThreshold(budgetDTO.getAlertThreshold());
        budget.setNotes(budgetDTO.getNotes());
        
        Budget updatedBudget = budgetService.createOrUpdateBudget(
            budget, userId, budgetDTO.getCategoryId());
        
        return ResponseEntity.ok(updatedBudget);
    }
    
    /**
     * Delete budget
     */
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long userId,
            @PathVariable Long budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get budget alerts for user
     */
    @GetMapping("/alerts")
    public ResponseEntity<List<Map<String, Object>>> getBudgetAlerts(@PathVariable Long userId) {
        List<Map<String, Object>> alerts = budgetService.getBudgetAlerts(userId);
        return ResponseEntity.ok(alerts);
    }
}