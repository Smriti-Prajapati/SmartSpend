package com.smartspend.controller;

import com.smartspend.model.Category;
import com.smartspend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/expense")
    public ResponseEntity<List<Category>> getExpenseCategories() {
        return ResponseEntity.ok(categoryRepository.findByTypeIn(
                List.of(Category.CategoryType.EXPENSE, Category.CategoryType.BOTH)));
    }
}
