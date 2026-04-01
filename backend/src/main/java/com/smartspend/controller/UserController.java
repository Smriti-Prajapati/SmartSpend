package com.smartspend.controller;

import com.smartspend.model.User;
import com.smartspend.repository.UserRepository;
import com.smartspend.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Long uid = SecurityUtils.getCurrentUserId();
        return userRepository.findById(uid).map(u -> {
            Map<String, Object> res = new HashMap<>();
            res.put("id",            u.getId());
            res.put("name",          u.getName());
            res.put("email",         u.getEmail());
            res.put("currency",      u.getCurrency() != null ? u.getCurrency() : "USD");
            res.put("monthlyBudget", u.getMonthlyBudget() != null ? u.getMonthlyBudget() : 0.0);
            res.put("createdAt",     u.getCreatedAt().toString());
            return ResponseEntity.ok(res);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates) {
        try {
            User user = userRepository.findById(SecurityUtils.getCurrentUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (updates.containsKey("name") && updates.get("name") != null)
                user.setName(updates.get("name").toString());

            if (updates.containsKey("currency") && updates.get("currency") != null)
                user.setCurrency(updates.get("currency").toString());

            if (updates.containsKey("monthlyBudget") && updates.get("monthlyBudget") != null) {
                String budgetStr = updates.get("monthlyBudget").toString().trim();
                if (!budgetStr.isEmpty() && !budgetStr.equals("0")) {
                    user.setMonthlyBudget(Double.parseDouble(budgetStr));
                }
            }

            userRepository.save(user);

            Map<String, Object> res = new HashMap<>();
            res.put("message",  "Profile updated successfully");
            res.put("name",     user.getName());
            res.put("currency", user.getCurrency());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }
}
