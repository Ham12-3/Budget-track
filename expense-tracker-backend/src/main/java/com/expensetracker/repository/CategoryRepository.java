package com.expensetracker.repository;

import com.expensetracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Category entity operations
 * Provides basic CRUD operations and custom query methods
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find category by name
     */
    Optional<Category> findByName(String name);
    
    /**
     * Find all system categories
     */
    List<Category> findByIsSystemTrue();
    
    /**
     * Find all user-created categories
     */
    List<Category> findByIsSystemFalse();
    
    /**
     * Check if category name exists
     */
    boolean existsByName(String name);
    
    /**
     * Find categories ordered by name
     */
    List<Category> findAllByOrderByNameAsc();
}