package com.nado.smartcare.openai.entity.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record ChatRequest(
        String message,
        String sessionId,  // 대화 세션 관리를 위한 ID
        Map<String, Object> metadata  // 추가 메타데이터 (예: 사용자 정보, 컨텍스트 등)
) {
    // 기본 생성자
    public ChatRequest {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지는 비어있을 수 없습니다.");
        }
    }

    // 메시지와 세션 ID만 있는 경우
    public ChatRequest(String message, String sessionId) {
        this(message, sessionId, new HashMap<>());
    }
}