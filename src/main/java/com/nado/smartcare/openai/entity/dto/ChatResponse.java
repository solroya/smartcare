package com.nado.smartcare.openai.entity.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public record ChatResponse(
        String message,
        String provider,  // AI 제공자 (openai 또는 ollama)
        String status,
        Map<String, Object> metadata
) {
    // 기본 성공 응답 생성
    public static ChatResponse success(String message, String provider) {
        return new ChatResponse(message, provider, "success", new HashMap<>());
    }

    // 에러 응답 생성
    public static ChatResponse error(String errorMessage) {
        return new ChatResponse(
                errorMessage,
                "system",
                "error",
                Map.of("timestamp", LocalDateTime.now().toString())
        );
    }
}