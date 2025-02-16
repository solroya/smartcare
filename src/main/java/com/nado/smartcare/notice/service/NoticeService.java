package com.nado.smartcare.notice.service;

import com.nado.smartcare.notice.dto.NoticeDto;
import com.nado.smartcare.page.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeService {

    List<NoticeDto> findAll(PageRequest pageRequest);

    List<NoticeDto> findByTitleContaining(String searchTerm, PageRequest pageRequest);

    // 공지사항 6개만 불러오기
    List<NoticeDto> findLatestNotices(int limit);

    NoticeDto findById(Long noticeNo);

    NoticeDto saveNotice(NoticeDto noticeDto);

    PageResponse<NoticeDto> getAllNotices(Pageable pageable);

}
