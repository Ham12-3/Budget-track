package com.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Expense Tracker Backend
 * This initializes the Spring Boot application with all necessary configurations
 */
@SpringBootApplication
public class ExpenseTrackerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }
}