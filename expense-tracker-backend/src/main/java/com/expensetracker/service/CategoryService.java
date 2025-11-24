package com.expensetracker.service;

import com.expensetracker.entity.Category;
import com.expensetracker.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Category-related business logic
 * Handles category management and initialization
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    /**
     * Initialize default system categories on startup
     */
    @PostConstruct
    public void initializeDefaultCategories() {
        if (categoryRepository.count() == 0) {
            List<Category> defaultCategories = Arrays.asList(
                createCategory("Food & Dining", "Restaurants, groceries, food delivery", "🍔", "#FF6384", true),
                createCategory("Transportation", "Gas, public transit, parking, taxi", "🚗", "#36A2EB", true),
                createCategory("Shopping", "Clothing, electronics, household items", "🛍️", "#FFCE56", true),
                createCategory("Entertainment", "Movies, games, hobbies, subscriptions", "🎬", "#4BC0C0", true),
                createCategory("Bills & Utilities", "Electricity, water, internet, phone", "💡", "#9966FF", true),
                createCategory("Healthcare", "Medical, dental, pharmacy, insurance", "🏥", "#FF9F40", true),
                createCategory("Education", "Courses, books, training materials", "📚", "#FF6384", true),
                createCategory("Travel", "Flights, hotels, vacation expenses", "✈️", "#C9CBCF", true),
                createCategory("Personal Care", "Haircuts, cosmetics, gym membership", "💅", "#4BC0C0", true),
                createCategory("Savings", "Emergency fund, investments", "💰", "#90EE90", true),
                createCategory("Income", "Salary, freelance, other income", "💵", "#32CD32", true),
                createCategory("Other", "Miscellaneous expenses", "📌", "#808080", true)
            );
            
            categoryRepository.saveAll(defaultCategories);
        }
    }
    
    /**
     * Helper method to create a category
     */
    private Category createCategory(String name, String description, String icon, String color, boolean isSystem) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIcon(icon);
        category.setColor(color);
        category.setSystem(isSystem);
        return category;
    }
    
    /**
     * Create a new custom category
     */
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
        }
        
        category.setSystem(false); // User-created categories are not system categories
        return categoryRepository.save(category);
    }
    
    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Get category by ID
     */
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    /**
     * Get category by name
     */
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    /**
     * Get system categories
     */
    public List<Category> getSystemCategories() {
        return categoryRepository.findByIsSystemTrue();
    }
    
    /**
     * Get user-created categories
     */
    public List<Category> getUserCategories() {
        return categoryRepository.findByIsSystemFalse();
    }
    
    /**
     * Update category
     */
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        
        // Don't allow updating system categories
        if (category.isSystem()) {
            throw new IllegalArgumentException("Cannot update system categories");
        }
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setIcon(categoryDetails.getIcon());
        category.setColor(categoryDetails.getColor());
        
        return categoryRepository.save(category);
    }
    
    /**
     * Delete category
     */
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        
        // Don't allow deleting system categories
        if (category.isSystem()) {
            throw new IllegalArgumentException("Cannot delete system categories");
        }
        
        // Check if category has transactions
        if (!category.getTransactions().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category with existing transactions");
        }
        
        categoryRepository.delete(category);
    }
}