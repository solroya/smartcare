package com.nado.smartcare.notice.service.impl;

import com.nado.smartcare.notice.domain.Notice;
import com.nado.smartcare.notice.dto.NoticeDto;
import com.nado.smartcare.notice.repository.NoticeRepository;
import com.nado.smartcare.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public List<NoticeDto> findAll() {
        List<Notice> result = noticeRepository.findAll();
        return result.stream()
                .map(NoticeDto::from)
                .toList();
    }

    @Override
    public List<NoticeDto> findLatestNotices(int limit) {
        List<Notice> notices = noticeRepository.findTop6ByOrderByCreatedAtDesc();
        if (notices == null || notices.isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }
        return notices.stream()
                .map(NoticeDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public NoticeDto findById(Long noticeNo) {
        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + noticeNo));
        return NoticeDto.from(notice);
    }

    @Override
    public NoticeDto saveNotice(NoticeDto noticeDto) {
        Notice notice = noticeDto.to();
        return NoticeDto.from(noticeRepository.save(notice));
    }

    @Override
    public NoticeDto updateNotice(Long noticeNo, String newTitle, String newContent, String newImage) {
        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new IllegalArgumentException("No notice found with id: " + noticeNo));

        notice.setTitle(newTitle);
        notice.setContent(newContent);
        notice.setEmployee(notice.getEmployee());
        Notice updatedNotice = noticeRepository.save(notice);
        return NoticeDto.from(updatedNotice);
    }

    @Override
    public void deleteNotice(Long noticeNo) {
        Notice notice = noticeRepository.findByNoticeNo(noticeNo)
                .orElseThrow(() -> new IllegalArgumentException("No notice found with id: " + noticeNo));
        noticeRepository.delete(notice);
    }
}
