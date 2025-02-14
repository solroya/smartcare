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

    /*
    *   총 요청 수
        성공률
        평균 응답 시간
        AI 제공자별 사용량
        에러 발생 패턴
    * */

    private final AIUsageService usageService;

   /* @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Map<String, Object> stats = usageService.getUsageStats(start, end);
        return ResponseEntity.ok(stats);

        *//*GET /api/ai-analytics/stats?start=2024-02-14T00:00:00&end=2024-02-14T23:59:59*//*
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        // 대시보드는 최근 24시간 데이터를 보여줍니다
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusHours(24);

        Map<String, Object> stats = usageService.getUsageStats(start, end);
        return ResponseEntity.ok(stats);

        *//*GET /api/ai-analytics/dashboard*//*
    }*/

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
