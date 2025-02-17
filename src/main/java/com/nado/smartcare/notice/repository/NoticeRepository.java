package com.nado.smartcare.notice.repository;

import com.nado.smartcare.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 공지사항 limit 만큼 가져오기
//    @Query(value = "SELECT * FROM notice ORDER BY created_at DESC FETCH FIRST :limit ROWS ONLY", nativeQuery = true)
//    List<Notice> findLatestNotices(@Param("limit") int limit);

    // Spring Data JPA 메서드 이름 규칙 사용: 최신 6개 공지사항을 가져옴
    List<Notice> findTop6ByOrderByCreatedAtDesc();

    // 제목으로 공지사항 검색
    Page<Notice> findByTitleContaining(String title, Pageable pageable);


}
