package com.nado.smartcare.aianalytics.entity.dto;

import com.nado.smartcare.aianalytics.entity.AIUsageStats;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AIUsageStatsDto {
    private Long id;
    private String provider;
    private String endpoint;
    private Long responseTime;
    private Integer tokenCount;
    private Boolean isSuccess;
    private String errorMessage;
    private String prompt;
    private String userMessage;
    private String aiResponse;
    private LocalDateTime createdAt;

    // Entity -> Dto
    public static AIUsageStatsDto from(AIUsageStats aiUsageStats) {
        AIUsageStatsDto dto = new AIUsageStatsDto();
        dto.setId(aiUsageStats.getId());
        dto.setProvider(aiUsageStats.getProvider());
        dto.setEndpoint(aiUsageStats.getEndpoint());
        dto.setResponseTime(aiUsageStats.getResponseTime());
        dto.setTokenCount(aiUsageStats.getTokenCount());
        dto.setIsSuccess(aiUsageStats.getIsSuccess());
        dto.setErrorMessage(aiUsageStats.getErrorMessage());
        dto.setPrompt(aiUsageStats.getPrompt());
        dto.setUserMessage(aiUsageStats.getUserMessage());
        dto.setAiResponse(aiUsageStats.getAiResponse());
        dto.setCreatedAt(aiUsageStats.getCreatedAt());
        return dto;
    }
}
