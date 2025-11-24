package com.expensetracker.service;

import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.Transaction.TransactionType;
import com.expensetracker.entity.User;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.repository.TransactionRepository.CategorySpending;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service layer for Transaction-related business logic
 * Handles expense tracking, filtering, and reporting
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    
    /**
     * Create a new transaction
     */
    public Transaction createTransaction(Transaction transaction, Long userId, Long categoryId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
        
        transaction.setUser(user);
        transaction.setCategory(category);
        
        // Set transaction date to today if not provided
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDate.now());
        }
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * Get transaction by ID
     */
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    
    /**
     * Get all transactions for a user with pagination
     */
    public Page<Transaction> getUserTransactions(Long userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable);
    }
    
    /**
     * Get transactions with filters
     */
    public Page<Transaction> getFilteredTransactions(
            Long userId, 
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            TransactionType type,
            Pageable pageable) {
        
        // Apply different filters based on provided parameters
        if (categoryId != null && startDate != null && endDate != null) {
            return transactionRepository.findByUserIdAndCategoryIdAndTransactionDateBetween(
                userId, categoryId, startDate, endDate, pageable);
        } else if (categoryId != null) {
            return transactionRepository.findByUserIdAndCategoryId(userId, categoryId, pageable);
        } else if (startDate != null && endDate != null) {
            return transactionRepository.findByUserIdAndTransactionDateBetween(
                userId, startDate, endDate, pageable);
        } else if (type != null) {
            return transactionRepository.findByUserIdAndType(userId, type, pageable);
        } else {
            return transactionRepository.findByUserId(userId, pageable);
        }
    }
    
    /**
     * Get monthly summary for a user
     */
    public Map<String, Object> getMonthlySummary(Long userId, int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        // Calculate totals
        BigDecimal totalIncome = transactionRepository.calculateTotalByUserAndTypeAndDateRange(
            userId, TransactionType.INCOME, startDate, endDate);
        BigDecimal totalExpenses = transactionRepository.calculateTotalByUserAndTypeAndDateRange(
            userId, TransactionType.EXPENSE, startDate, endDate);
        
        // Get category breakdown
        List<CategorySpending> categoryBreakdown = transactionRepository.getCategoryBreakdown(
            userId, TransactionType.EXPENSE, startDate, endDate);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("month", month);
        summary.put("year", year);
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpenses", totalExpenses);
        summary.put("balance", totalIncome.subtract(totalExpenses));
        summary.put("categoryBreakdown", categoryBreakdown);
        
        return summary;
    }
    
    /**
     * Get spending by category for a date range
     */
    public BigDecimal getCategorySpending(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.calculateTotalByCategoryAndDateRange(
            userId, categoryId, TransactionType.EXPENSE, startDate, endDate);
    }
    
    /**
     * Get recent transactions for a user
     */
    public List<Transaction> getRecentTransactions(Long userId) {
        return transactionRepository.findTop10ByUserIdOrderByTransactionDateDescCreatedAtDesc(userId);
    }
    
    /**
     * Update transaction
     */
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
        
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setDescription(transactionDetails.getDescription());
        transaction.setTransactionDate(transactionDetails.getTransactionDate());
        transaction.setType(transactionDetails.getType());
        
        // Update category if provided
        if (transactionDetails.getCategory() != null) {
            Category category = categoryRepository.findById(transactionDetails.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            transaction.setCategory(category);
        }
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * Delete transaction
     */
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }
    
    /**
     * Get yearly summary for a user
     */
    public Map<String, Object> getYearlySummary(Long userId, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        BigDecimal totalIncome = transactionRepository.calculateTotalByUserAndTypeAndDateRange(
            userId, TransactionType.INCOME, startDate, endDate);
        BigDecimal totalExpenses = transactionRepository.calculateTotalByUserAndTypeAndDateRange(
            userId, TransactionType.EXPENSE, startDate, endDate);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("year", year);
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpenses", totalExpenses);
        summary.put("balance", totalIncome.subtract(totalExpenses));
        summary.put("averageMonthlyExpense", totalExpenses.divide(BigDecimal.valueOf(12), BigDecimal.ROUND_HALF_UP));
        
        return summary;
    }
}