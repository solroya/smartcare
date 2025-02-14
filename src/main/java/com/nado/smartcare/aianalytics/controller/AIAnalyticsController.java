package com.nado.smartcare.aianalytics.controller;

import com.nado.smartcare.aianalytics.entity.AIUsageStats;
import com.nado.smartcare.aianalytics.service.AIUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-analytics")
@RequiredArgsConstructor
public class AIAnalyticsController {

    private final AIUsageService usageService;

    @GetMapping("/search")
    public ResponseEntity<List<AIUsageStats>> searchConversations(
            @RequestParam String keyword,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(usageService.searchConversations(keyword, start, end));
    }

    @GetMapping("/analysis")
    public ResponseEntity<Map<String, Object>> getConversationAnalysis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(usageService.analyzeConversations(start, end));
    }
}
