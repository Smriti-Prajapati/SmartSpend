package com.smartspend.controller;

import com.smartspend.service.InsightService;
import com.smartspend.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/insights")
public class InsightController {

    @Autowired private InsightService insightService;

    @GetMapping
    public ResponseEntity<?> getInsights() {
        return ResponseEntity.ok(insightService.getInsights(SecurityUtils.getCurrentUserId()));
    }
}
