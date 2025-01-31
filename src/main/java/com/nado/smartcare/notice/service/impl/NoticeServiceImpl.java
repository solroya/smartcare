package com.nado.smartcare.notice.service.impl;

import com.nado.smartcare.notice.domain.Notice;
import com.nado.smartcare.notice.dto.NoticeDto;
import com.nado.smartcare.notice.repository.NoticeRepository;
import com.nado.smartcare.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public List<NoticeDto> findAll(PageRequest pageRequest) {
        return noticeRepository.findAll(pageRequest)
                .stream()
                .map(NoticeDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<NoticeDto> findByTitleContaining(String searchTerm, PageRequest pageRequest) {
        return noticeRepository.findByTitleContaining(searchTerm, pageRequest)
                .stream()
                .map(NoticeDto::from)
                .collect(Collectors.toList());
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

    @Transactional
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

    @Override
    public List<NoticeDto> findByTitleContainingAndDateBetween(String searchTerm, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return noticeRepository.findByTitleContainingAndCreatedAtBetween(
                        searchTerm,
                        startDateTime,
                        endDateTime,
                        pageRequest)
                .stream()
                .map(NoticeDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<NoticeDto> findByDateBetween(LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        log.info("Searching notices between {} and {}", startDateTime, endDateTime);

        return noticeRepository.findByCreatedAtBetween(
                        startDateTime,
                        endDateTime,
                        pageRequest)
                .stream()
                .map(noticeDto -> NoticeDto.from(noticeDto))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void incrementViewCount(Long noticeNo) {
        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found with id: " + noticeNo));
//        notice.incrementViewCount();
    }
}
