package com.nado.smartcare.aianalytics.service;

import com.nado.smartcare.aianalytics.entity.AIUsageStats;
import com.nado.smartcare.aianalytics.entity.dto.AIUsageStatsDto;
import com.nado.smartcare.page.PageResponse;
import com.nado.smartcare.aianalytics.repository.AIUsageStatsRepository;
import com.nado.smartcare.openai.service.AIModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect // AOP 기능을 사용하기 위한 어노테이션
@Service
@Log4j2
@RequiredArgsConstructor
public class AIUsageService {

    private final AIUsageStatsRepository usageStatsRepository;
    private final AIModelService aiModelService; // 현재 AI 제공자 정보를 가져오기 위해 필요

    // AI 서비스의 모든 응답 메서드를 가로채서 사용량을 기록
    @Around("execution(* com.nado.smartcare.openai.service.AIModelService.getResponseWithPrompt(..))")
    public Object trackUsage(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("AOP 시작: {}", joinPoint.getSignature().getName());

        // 시작 시간 기록
        long startTime = System.currentTimeMillis();

        // 현재 AI 제공자 확인
        String provider = aiModelService.getCurrentProvider();

        // 메서드 이름에서 엔드포인트 추출
        String endpoint = joinPoint.getSignature().getName();

        // 사용자 메시지와 프롬프트 추출
        // 메서드 파라미터 추출
        Object[] args = joinPoint.getArgs();

        // 프롬프트 템플릿과 파라미터 분리
        String promptTemplate = "";
        Map<String, Object> parameters = new HashMap<>();

        if (args.length >= 2 && args[1] instanceof Map) {
            promptTemplate = args[0].toString();
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) args[1];
            parameters = params;
        }

        // 사용자 메시지 추출
        String userMessage = parameters.containsKey("input") ?
                parameters.get("input").toString() : "unknown input";

        // 전체 프롬프트 구성
        String fullPrompt = constructFullPrompt(promptTemplate, parameters);

        log.debug("추출된 사용자 메시지: {}", userMessage);
        log.debug("구성된 전체 프롬프트: {}", fullPrompt);

        // 통계 객체 생성
        AIUsageStats stats = new AIUsageStats(provider, endpoint, fullPrompt, userMessage);

        try {
            // 실제 메서드 실행
            Object result = joinPoint.proceed();
            String aiResponse = result.toString();

            // 응답 시간 계산
            long responseTime = System.currentTimeMillis() - startTime;

            // 토큰 수 계산
            int tokenCount = calculateTokenCount(provider, fullPrompt, aiResponse);

            // 성공 정보 기록
            stats.recordResponse(responseTime, tokenCount, aiResponse);

            return result;
        } catch (Exception e) {
            stats.recordError(e.getMessage());
            throw e;
        } finally {
            // 통계 저장
            usageStatsRepository.save(stats);
            log.debug("AOP 종료: {}", joinPoint.getSignature().getName());
        }
    }

    // 전체 프롬프트 구성 메서드
    private String constructFullPrompt(String template, Map<String, Object> parameters) {
        StringBuilder fullPrompt = new StringBuilder();

        // 템플릿 추가
        fullPrompt.append("Template:\n").append(template).append("\n\n");

        // 컨텍스트 추가 (있는 경우)
        if (parameters.containsKey("documents")) {
            fullPrompt.append("Context:\n")
                    .append(parameters.get("documents"))
                    .append("\n\n");
        }

        // 사용자 입력 추가
        fullPrompt.append("User Input:\n")
                .append(parameters.containsKey("input") ?
                        parameters.get("input").toString() :
                        "unknown input");

        return fullPrompt.toString();
    }

    // 사용자 메시지 추출 메서드
    private String extractUserMessage(Object[] args) {
        if (args.length > 0 && args[0] != null) {
            if (args[0] instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) args[0];
                // input 파라미터가 있는 경우 이를 사용자 메시지로 간주
                return map.containsKey("input") ?
                        map.get("input").toString() :
                        "unknown message";
            }
            // Map이 아닌 경우 첫 번째 인자를 사용자 메시지로 간주
            return args[0].toString();
        }
        return "unknown message";
    }

    // 프롬프트 추출 메서드
    private String extractPrompt(Object[] args) {
        if (args.length > 0 && args[0] != null) {
            if (args[0] instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) args[0];
                StringBuilder fullPrompt = new StringBuilder();

                // 시스템 프롬프트 추가 (있는 경우)
                if (map.containsKey("documents")) {
                    fullPrompt.append("Context:\n")
                            .append(map.get("documents"))
                            .append("\n\n");
                }

                // 기본 프롬프트 템플릿 추가 (있는 경우)
                if (map.containsKey("prompt")) {
                    fullPrompt.append("Template:\n")
                            .append(map.get("prompt"))
                            .append("\n\n");
                }

                // 사용자 입력 추가
                fullPrompt.append("User Input:\n")
                        .append(map.containsKey("input") ?
                                map.get("input").toString() :
                                "unknown input");

                return fullPrompt.toString();
            }
            // Map이 아닌 경우 전체 내용을 프롬프트로 저장
            return "Direct Input:\n" + args[0].toString();
        }
        return "unknown prompt";
    }

    // 토큰 수 계산 메서드
    private int calculateTokenCount(String provider, String prompt, String response) {
        // OpenAI의 경우 GPT 토크나이저 기준으로 계산
        if ("openai".equals(provider)) {
            return estimateGPTTokens(prompt) + estimateGPTTokens(response);
        }
        // Ollama의 경우 단순 단어 수 기준으로 계산
        else {
            return (prompt + " " + response).split("\\s+").length;
        }
    }

    // GPT 토큰 수 추정 메서드 (간단한 구현)
    private int estimateGPTTokens(String text) {
        // 일반적으로 영어 기준 1토큰 ≈ 4자
        return text.length() / 4;
    }

    // 통계 조회 메서드
    public Map<String, Object> getUsageStats() {
        Map<String, Object> stats = new HashMap<>();

        // 전체 요청 수 조회
        long totalRequests = usageStatsRepository.count();
        stats.put("totalRequests", totalRequests);

        if (totalRequests > 0) {
            double successRate = usageStatsRepository.calculateTotalSuccessRate();
            double avgResponseTime = usageStatsRepository.calculateTotalAverageResponseTime();

            // 토큰 관련 통계 추가
            long totalTokens = usageStatsRepository.calculateTotalTokens();
            double avgTokens = usageStatsRepository.calculateAverageTokens();
            long maxTokens = usageStatsRepository.calculateMaxTokens();

            stats.put("successRate", String.format("%.2f%%", successRate));
            stats.put("avgResponseTime", String.format("%.2fms", avgResponseTime));
            stats.put("totalTokens", totalTokens);
            stats.put("avgTokens", String.format("%.1f", avgTokens));
            stats.put("maxTokens", maxTokens);
        } else {
            stats.put("successRate", "0.00%");
            stats.put("avgResponseTime", "0.00ms");
            stats.put("totalTokens", 0);
            stats.put("avgTokens", "0.0");
            stats.put("maxTokens", 0);
        }

        return stats;
    }

    // 모든 대화 내역을 조회하는 메서드
    public List<AIUsageStats> getAllConversations() {
        return usageStatsRepository.findAllByOrderByCreatedAtDesc();
    }


    // 키워드로 대화 검색하는 메서드
    public List<AIUsageStats> searchConversations(String keyword, LocalDateTime start, LocalDateTime end) {
        return usageStatsRepository.searchConversations(keyword, start, end);
    }

    // 대화 분석 결과를 더 의미있게 표현하는 메서드
    public Map<String, Object> analyzeConversations(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> analysis = new HashMap<>();

        // 자주 묻는 질문 top 5
        analysis.put("topQuestions",
                usageStatsRepository.findMostFrequentQuestions(start, end)
                        .stream()
                        .limit(5)
                        .collect(Collectors.toList())
        );

        // 대화 메트릭스
        Map<String, Object> metrics = usageStatsRepository.calculateConversationMetrics(start, end);
        analysis.put("metrics", metrics);

        return analysis;
    }

    public PageResponse<AIUsageStatsDto> getAIUsageStats(int page, int size) {
        // 페이지는 0부터 시작이므로 -1
        Pageable pageable = PageRequest.of(page - 1, size);

        // 내역을 조회하고 DTO로 변환
        Page<AIUsageStats> pages = usageStatsRepository.findAllByOrderByCreatedAtDesc(pageable);

        // Entity -> Dto
        Page<AIUsageStatsDto> dtos = pages.map(AIUsageStatsDto::from);

        return PageResponse.from(dtos);
    }

}
