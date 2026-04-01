package com.smartspend.service;

import com.smartspend.dto.ExpenseDTO;
import com.smartspend.model.*;
import com.smartspend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;

    public ExpenseDTO create(Long userId, ExpenseDTO dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Category cat = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        Expense e = new Expense();
        e.setTitle(dto.getTitle()); e.setAmount(dto.getAmount());
        e.setDate(dto.getDate()); e.setDescription(dto.getDescription());
        e.setCategory(cat); e.setUser(user);
        return toDTO(expenseRepository.save(e));
    }

    public ExpenseDTO update(Long userId, Long id, ExpenseDTO dto) {
        Expense e = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!e.getUser().getId().equals(userId)) throw new RuntimeException("Unauthorized");
        Category cat = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        e.setTitle(dto.getTitle()); e.setAmount(dto.getAmount());
        e.setDate(dto.getDate()); e.setDescription(dto.getDescription());
        e.setCategory(cat);
        return toDTO(expenseRepository.save(e));
    }

    public void delete(Long userId, Long id) {
        Expense e = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!e.getUser().getId().equals(userId)) throw new RuntimeException("Unauthorized");
        expenseRepository.delete(e);
    }

    public List<ExpenseDTO> getAll(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ExpenseDTO> getByDateRange(Long userId, LocalDate start, LocalDate end) {
        return expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, start, end).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ExpenseDTO> search(Long userId, String keyword) {
        return expenseRepository.searchByKeyword(userId, keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ExpenseDTO> getByCategory(Long userId, Long categoryId) {
        return expenseRepository.findByUserIdAndCategoryIdOrderByDateDesc(userId, categoryId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ExpenseDTO toDTO(Expense e) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(e.getId()); dto.setTitle(e.getTitle());
        dto.setAmount(e.getAmount()); dto.setDate(e.getDate());
        dto.setDescription(e.getDescription());
        dto.setCategoryId(e.getCategory().getId());
        dto.setCategoryName(e.getCategory().getName());
        dto.setCategoryColor(e.getCategory().getColor());
        dto.setCategoryIcon(e.getCategory().getIcon());
        return dto;
    }
}
