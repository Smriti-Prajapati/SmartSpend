package com.smartspend.controller;

import com.smartspend.dto.*;
import com.smartspend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try { return ResponseEntity.ok(authService.register(req)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest req) {
        try { return ResponseEntity.ok(authService.login(req)); }
        catch (Exception e) { return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password")); }
    }
}
