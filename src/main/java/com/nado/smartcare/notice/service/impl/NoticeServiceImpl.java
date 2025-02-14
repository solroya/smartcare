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

}
