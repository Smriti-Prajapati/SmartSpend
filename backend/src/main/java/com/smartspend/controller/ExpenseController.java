package com.smartspend.controller;

import com.smartspend.dto.ExpenseDTO;
import com.smartspend.service.ExpenseService;
import com.smartspend.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired private ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {
        Long uid = SecurityUtils.getCurrentUserId();
        if (search != null && !search.isBlank()) return ResponseEntity.ok(expenseService.search(uid, search));
        if (startDate != null && endDate != null) return ResponseEntity.ok(expenseService.getByDateRange(uid, startDate, endDate));
        if (categoryId != null) return ResponseEntity.ok(expenseService.getByCategory(uid, categoryId));
        return ResponseEntity.ok(expenseService.getAll(uid));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ExpenseDTO dto) {
        try { return ResponseEntity.ok(expenseService.create(SecurityUtils.getCurrentUserId(), dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ExpenseDTO dto) {
        try { return ResponseEntity.ok(expenseService.update(SecurityUtils.getCurrentUserId(), id, dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { expenseService.delete(SecurityUtils.getCurrentUserId(), id); return ResponseEntity.ok(Map.of("message", "Deleted")); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
}
