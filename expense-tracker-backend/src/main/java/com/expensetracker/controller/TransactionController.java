package com.expensetracker.controller;

import com.expensetracker.dto.TransactionDTO;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.Transaction.TransactionType;
import com.expensetracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Transaction management
 * Handles expense and income tracking with advanced filtering and pagination
 */
@RestController
@RequestMapping("/users/{userId}/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    
    /**
     * Get all transactions for a user with pagination and filtering
     */
    @GetMapping
    public ResponseEntity<Page<Transaction>> getTransactions(
            @PathVariable Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? 
            Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Transaction> transactions = transactionService.getFilteredTransactions(
            userId, categoryId, startDate, endDate, type, pageable);
        
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Get recent transactions for quick view
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Transaction>> getRecentTransactions(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.getRecentTransactions(userId);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Get transaction by ID
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(
            @PathVariable Long userId,
            @PathVariable Long transactionId) {
        return transactionService.getTransactionById(transactionId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create new transaction
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @PathVariable Long userId,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setTransactionDate(transactionDTO.getTransactionDate());
        transaction.setType(transactionDTO.getType());
        
        Transaction createdTransaction = transactionService.createTransaction(
            transaction, userId, transactionDTO.getCategoryId());
        
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }
    
    /**
     * Update transaction
     */
    @PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long userId,
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setTransactionDate(transactionDTO.getTransactionDate());
        transaction.setType(transactionDTO.getType());
        
        // Set category if provided
        if (transactionDTO.getCategoryId() != null) {
            com.expensetracker.entity.Category tempCategory = new com.expensetracker.entity.Category();
            tempCategory.setId(transactionDTO.getCategoryId());
            transaction.setCategory(tempCategory);
        }
        
        Transaction updatedTransaction = transactionService.updateTransaction(transactionId, transaction);
        return ResponseEntity.ok(updatedTransaction);
    }
    
    /**
     * Delete transaction
     */
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long userId,
            @PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get monthly summary
     */
    @GetMapping("/summary/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlySummary(
            @PathVariable Long userId,
            @RequestParam int month,
            @RequestParam int year) {
        Map<String, Object> summary = transactionService.getMonthlySummary(userId, month, year);
        return ResponseEntity.ok(summary);
    }
    
    /**
     * Get yearly summary
     */
    @GetMapping("/summary/yearly")
    public ResponseEntity<Map<String, Object>> getYearlySummary(
            @PathVariable Long userId,
            @RequestParam int year) {
        Map<String, Object> summary = transactionService.getYearlySummary(userId, year);
        return ResponseEntity.ok(summary);
    }
    
    /**
     * Get category spending for a date range
     */
    @GetMapping("/spending/category/{categoryId}")
    public ResponseEntity<BigDecimal> getCategorySpending(
            @PathVariable Long userId,
            @PathVariable Long categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        BigDecimal spending = transactionService.getCategorySpending(
            userId, categoryId, startDate, endDate);
        return ResponseEntity.ok(spending);
    }
}