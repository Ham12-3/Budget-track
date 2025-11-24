package com.expensetracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Category entity for organizing expenses
 * Categories can be system-defined or user-created
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Category name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 50)
    private String icon; // Icon name for UI display
    
    @Column(length = 7)
    private String color; // Hex color code for UI display
    
    @Column(name = "is_system")
    @JsonProperty("isSystem")  // Force JSON to use "isSystem" instead of "system"
    private boolean isSystem = false; // System categories cannot be deleted
    
    // Relationships - Ignored in JSON responses to prevent lazy loading issues
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();
    
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Budget> budgets = new ArrayList<>();
}