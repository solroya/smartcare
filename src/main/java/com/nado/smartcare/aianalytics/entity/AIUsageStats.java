package com.nado.smartcare.aianalytics.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_usage_stats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class AIUsageStats {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // AI 제공자 정보 (openai 또는 ollama)
    private String provider;

    // 호출된 API 엔드포인트 (예: /api/ai/answer)
    private String endpoint;

    // 요청부터 응답까지 걸린 시간 (밀리초)
    private Long responseTime;

    // 요청에 사용된 토큰 수 (나중에 비용 계산에 활용 가능)
    private Integer tokenCount;

    // 요청 성공 여부
    private Boolean isSuccess;

    // 실패했을 경우의 에러 메시지
    @Column(length = 4000)
    private String errorMessage;

    // 사용된 프롬프트 (프롬프트 최적화에 활용 가능)
/*    @Column(columnDefinition = "TEXT")*/
    @Column(columnDefinition = "CLOB") // 오라클 데이터 베이스용
    private String prompt;

    // 사용자의 질문/메시지
    @Column(columnDefinition = "CLOB")
    private String userMessage;

    // AI의 응답
    @Column(columnDefinition = "CLOB")
    private String aiResponse;

    // 요청이 발생한 시간
    private LocalDateTime createdAt;

    // 새로운 사용량 기록을 생성하는 생성자
    public AIUsageStats(String provider, String endpoint, String prompt, String userMessage) {
        this.provider = provider;
        this.endpoint = endpoint;
        this.prompt = prompt;
        this.userMessage = userMessage;
        this.createdAt = LocalDateTime.now();
    }

    // 성공적인 응답을 기록하는 메서드
    public void recordResponse(Long responseTime, Integer tokenCount, String aiResponse) {
        this.responseTime = responseTime;
        this.tokenCount = tokenCount;
        this.aiResponse = aiResponse;
        this.isSuccess = true;
    }

    // 에러를 기록하는 메서드
    public void recordError(String errorMessage) {
        this.isSuccess = false;
        // 에러 메시지가 너무 길 경우 잘라내기
        if (errorMessage != null && errorMessage.length() > 4000) {
            this.errorMessage = errorMessage.substring(0, 4000);
        } else {
            this.errorMessage = errorMessage;
        }
    }
}
