package com.smartspend.config;

import com.smartspend.model.Category;
import com.smartspend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds default categories on startup if the table is empty.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) return; // already seeded

        List<Category> categories = List.of(
            cat("Food & Dining",    "F",  "#f97316", Category.CategoryType.EXPENSE),
            cat("Travel",           "T",  "#3b82f6", Category.CategoryType.EXPENSE),
            cat("Bills & Utilities","B",  "#8b5cf6", Category.CategoryType.EXPENSE),
            cat("Shopping",         "S",  "#ec4899", Category.CategoryType.EXPENSE),
            cat("Entertainment",    "E",  "#f59e0b", Category.CategoryType.EXPENSE),
            cat("Health",           "H",  "#10b981", Category.CategoryType.EXPENSE),
            cat("Education",        "Ed", "#6366f1", Category.CategoryType.EXPENSE),
            cat("Transport",        "Tr", "#14b8a6", Category.CategoryType.EXPENSE),
            cat("Groceries",        "G",  "#84cc16", Category.CategoryType.EXPENSE),
            cat("Other",            "O",  "#6b7280", Category.CategoryType.EXPENSE),
            cat("Salary",           "Sa", "#10b981", Category.CategoryType.INCOME),
            cat("Freelance",        "Fr", "#3b82f6", Category.CategoryType.INCOME),
            cat("Investment",       "In", "#f59e0b", Category.CategoryType.INCOME),
            cat("Gift",             "Gi", "#ec4899", Category.CategoryType.INCOME),
            cat("Other Income",     "Oi", "#6b7280", Category.CategoryType.INCOME)
        );

        categoryRepository.saveAll(categories);
        System.out.println("✅ Default categories seeded.");
    }

    private Category cat(String name, String icon, String color, Category.CategoryType type) {
        Category c = new Category();
        c.setName(name);
        c.setIcon(icon);
        c.setColor(color);
        c.setType(type);
        return c;
    }
}
