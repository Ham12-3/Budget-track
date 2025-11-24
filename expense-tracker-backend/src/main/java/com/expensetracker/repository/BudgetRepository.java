package com.expensetracker.repository;

import com.expensetracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Budget entity operations
 * Provides budget management queries
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    /**
     * Find budget for a specific user, category, month and year
     */
    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
        Long userId, 
        Long categoryId, 
        Integer month, 
        Integer year
    );
    
    /**
     * Find all budgets for a user in a specific month
     */
    List<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);
    
    /**
     * Find all budgets for a user
     */
    List<Budget> findByUserId(Long userId);
    
    /**
     * Check if budget exists for user and category in a specific period
     */
    boolean existsByUserIdAndCategoryIdAndMonthAndYear(
        Long userId, 
        Long categoryId, 
        Integer month, 
        Integer year
    );
    
    /**
     * Get budget with spent amount calculation
     */
    @Query("SELECT b as budget, " +
           "(SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = b.user.id " +
           "AND t.category.id = b.category.id " +
           "AND t.type = 'EXPENSE' " +
           "AND MONTH(t.transactionDate) = b.month " +
           "AND YEAR(t.transactionDate) = b.year) as spent " +
           "FROM Budget b " +
           "WHERE b.user.id = :userId " +
           "AND b.month = :month " +
           "AND b.year = :year")
    List<BudgetWithSpent> findBudgetsWithSpent(
        @Param("userId") Long userId,
        @Param("month") Integer month,
        @Param("year") Integer year
    );
    
    /**
     * Interface for budget with spent amount projection
     */
    interface BudgetWithSpent {
        Budget getBudget();
        java.math.BigDecimal getSpent();
    }
}