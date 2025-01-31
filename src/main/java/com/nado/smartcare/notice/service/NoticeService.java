package com.nado.smartcare.notice.service;

import com.nado.smartcare.notice.dto.NoticeDto;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface NoticeService {

    List<NoticeDto> findAll(PageRequest pageRequest);

    List<NoticeDto> findByTitleContaining(String searchTerm, PageRequest pageRequest);

    // 공지사항 6개만 불러오기
    List<NoticeDto> findLatestNotices(int limit);

    NoticeDto findById(Long noticeNo);

    NoticeDto saveNotice(NoticeDto noticeDto);

    NoticeDto updateNotice(Long noticeNo, String newTitle, String newContent, String newImage);

    void deleteNotice(Long noticeNo);

    // 제목 검색, 날짜 범위 공지사항 조회
    List<NoticeDto> findByTitleContainingAndDateBetween(String searchTerm,
                                                        LocalDate startDate,
                                                        LocalDate endDate,
                                                        PageRequest pageRequest);

    // 날짜 범위로 공지사항 조회
    List<NoticeDto> findByDateBetween(LocalDate startDate, LocalDate endDate, PageRequest pageRequest);

    // 조회수
    void incrementViewCount(Long noticeNo);

}
