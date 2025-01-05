package com.nado.smartcare.notice.service;

import com.nado.smartcare.notice.dto.NoticeDto;

import java.util.List;

public interface NoticeService {

    List<NoticeDto> findAll();

    // 공지사항 6개만 불러오기
    List<NoticeDto> findLatestNotices(int limit);

    NoticeDto findById(Long noticeNo);

    NoticeDto saveNotice(NoticeDto noticeDto);

    NoticeDto updateNotice(Long noticeNo, String newTitle, String newContent, String newImage);

    void deleteNotice(Long noticeNo);

}
