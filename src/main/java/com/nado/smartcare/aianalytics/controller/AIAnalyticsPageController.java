package com.nado.smartcare.aianalytics.controller;

import com.nado.smartcare.aianalytics.entity.dto.AIUsageStatsDto;
import com.nado.smartcare.page.PageResponse;
import com.nado.smartcare.aianalytics.repository.AIUsageStatsRepository;
import com.nado.smartcare.aianalytics.service.AIUsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/erp/ai-analytics")
@RequiredArgsConstructor
@Log4j2
public class AIAnalyticsPageController {
    private final AIUsageService aiUsageService;
    private final AIUsageStatsRepository usageStatsRepository;

    // 통계 페이지 뷰
    @GetMapping
    public String showAnalytics() {
        return "erp/ai/ai-analytics";
    }

    // 단순화된 통계 API
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = aiUsageService.getUsageStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("통계 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 전체 대화 내역 조회 API
    @GetMapping("/api/conversations")
    @ResponseBody
    public ResponseEntity<PageResponse<AIUsageStatsDto>> getConversations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            PageResponse<AIUsageStatsDto> conversations = aiUsageService.getAIUsageStats(page, size);
//            log.info("호출된 conversations: {}", conversations.toString());
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("대화 내역 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
