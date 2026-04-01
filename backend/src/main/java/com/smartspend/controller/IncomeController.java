package com.smartspend.controller;

import com.smartspend.dto.IncomeDTO;
import com.smartspend.service.IncomeService;
import com.smartspend.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    @Autowired private IncomeService incomeService;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long uid = SecurityUtils.getCurrentUserId();
        if (startDate != null && endDate != null) return ResponseEntity.ok(incomeService.getByDateRange(uid, startDate, endDate));
        return ResponseEntity.ok(incomeService.getAll(uid));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody IncomeDTO dto) {
        try { return ResponseEntity.ok(incomeService.create(SecurityUtils.getCurrentUserId(), dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody IncomeDTO dto) {
        try { return ResponseEntity.ok(incomeService.update(SecurityUtils.getCurrentUserId(), id, dto)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try { incomeService.delete(SecurityUtils.getCurrentUserId(), id); return ResponseEntity.ok(Map.of("message", "Deleted")); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
}
