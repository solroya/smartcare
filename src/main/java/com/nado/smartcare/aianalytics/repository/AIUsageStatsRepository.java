package com.nado.smartcare.aianalytics.repository;

import com.nado.smartcare.aianalytics.entity.AIUsageStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AIUsageStatsRepository extends JpaRepository<AIUsageStats, Long> {

    // 가장 자주 묻는 질문 찾기
    @Query(value = """
        SELECT user_message, COUNT(*) as frequency 
        FROM ai_usage_stats 
        WHERE created_at BETWEEN :start AND :end 
        GROUP BY user_message 
        HAVING COUNT(*) > 1 
        ORDER BY frequency DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findMostFrequentQuestions(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 대화 메트릭스 계산
    @Query("""
        SELECT NEW map(
            AVG(LENGTH(a.userMessage)) as avgUserMessageLength,
            AVG(LENGTH(a.aiResponse)) as avgAiResponseLength,
            MIN(LENGTH(a.userMessage)) as minUserMessageLength,
            MAX(LENGTH(a.userMessage)) as maxUserMessageLength
        )
        FROM AIUsageStats a
        WHERE a.createdAt BETWEEN :start AND :end
        """)
    Map<String, Object> calculateConversationMetrics(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 키워드 검색 - @Query 어노테이션을 사용하여 명시적으로 쿼리 정의
    @Query("""
        SELECT a FROM AIUsageStats a 
        WHERE (LOWER(a.userMessage) LIKE LOWER(CONCAT('%', :keyword, '%')) 
           OR LOWER(a.aiResponse) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND a.createdAt BETWEEN :start AND :end
        ORDER BY a.createdAt DESC
        """)
    List<AIUsageStats> searchConversations(
            @Param("keyword") String keyword,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<AIUsageStats> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);

    // 전체 성공률 계산
    @Query(value = """
        SELECT DECODE(COUNT(*), 0, 0,
            (CAST(COUNT(CASE WHEN is_success = 1 THEN 1 END) AS FLOAT) / 
             CAST(COUNT(*) AS FLOAT) * 100))
        FROM ai_usage_stats
        """, nativeQuery = true)
    double calculateTotalSuccessRate();

    // 전체 평균 응답 시간 계산
    @Query(value = """
        SELECT DECODE(COUNT(*), 0, 0,
            AVG(CAST(response_time AS FLOAT)))
        FROM ai_usage_stats 
        WHERE is_success = 1
        """, nativeQuery = true)
    double calculateTotalAverageResponseTime();

    // 모든 대화 내역을 최신순으로 조회
    List<AIUsageStats> findAllByOrderByCreatedAtDesc();

    // 총 토큰 사용량 조회
    @Query("SELECT COALESCE(SUM(a.tokenCount), 0) FROM AIUsageStats a")
    long calculateTotalTokens();

    // 평균 토큰 사용량 조회
    @Query("SELECT COALESCE(AVG(a.tokenCount), 0) FROM AIUsageStats a")
    double calculateAverageTokens();

    // 최대 토큰 사용량 조회
    @Query("SELECT COALESCE(MAX(a.tokenCount), 0) FROM AIUsageStats a")
    long calculateMaxTokens();

    // 페이징 처리(서버 사이드)
    Page<AIUsageStats> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
