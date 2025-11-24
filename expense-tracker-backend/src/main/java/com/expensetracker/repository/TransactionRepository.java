package com.expensetracker.repository;

import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.Transaction.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Transaction entity operations
 * Provides advanced querying with pagination and filtering capabilities
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Find all transactions for a specific user with pagination
     */
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find transactions by user and date range with pagination
     */
    Page<Transaction> findByUserIdAndTransactionDateBetween(
        Long userId, 
        LocalDate startDate, 
        LocalDate endDate, 
        Pageable pageable
    );
    
    /**
     * Find transactions by user and category with pagination
     */
    Page<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);
    
    /**
     * Find transactions by user, category and date range
     */
    Page<Transaction> findByUserIdAndCategoryIdAndTransactionDateBetween(
        Long userId,
        Long categoryId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );
    
    /**
     * Find transactions by user and type
     */
    Page<Transaction> findByUserIdAndType(Long userId, TransactionType type, Pageable pageable);
    
    /**
     * Calculate total spending for a user in a date range
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.type = :type " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalByUserAndTypeAndDateRange(
        @Param("userId") Long userId,
        @Param("type") TransactionType type,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Calculate total spending by category for a user in a date range
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.category.id = :categoryId " +
           "AND t.type = :type " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalByCategoryAndDateRange(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId,
        @Param("type") TransactionType type,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Get spending breakdown by category for a user in a date range
     */
    @Query("SELECT t.category.id as categoryId, " +
           "t.category.name as categoryName, " +
           "SUM(t.amount) as total " +
           "FROM Transaction t " +
           "WHERE t.user.id = :userId " +
           "AND t.type = :type " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.category.id, t.category.name " +
           "ORDER BY total DESC")
    List<CategorySpending> getCategoryBreakdown(
        @Param("userId") Long userId,
        @Param("type") TransactionType type,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Interface for category spending projection
     */
    interface CategorySpending {
        Long getCategoryId();
        String getCategoryName();
        BigDecimal getTotal();
    }
    
    /**
     * Find recent transactions for a user
     */
    List<Transaction> findTop10ByUserIdOrderByTransactionDateDescCreatedAtDesc(Long userId);
}