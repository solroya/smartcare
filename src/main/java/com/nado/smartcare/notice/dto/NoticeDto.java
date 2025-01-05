package com.nado.smartcare.notice.dto;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.notice.domain.Notice;
import com.nado.smartcare.notice.domain.NoticeImage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class NoticeDto {

    private Long noticeNo;

    private String title;

    private String content;

    private Employee employee;

    private List<String> imagePaths;

    private Long viewCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public Notice to() {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setEmployee(employee);
        return notice;
    }

    public static NoticeDto from(Notice notice) {
        return NoticeDto.builder()
                .noticeNo(notice.getNoticeNo())
                .title(notice.getTitle())
                .content(notice.getContent())
                .employee(notice.getEmployee())
                .imagePaths(notice.getImages().stream()
                        .map(NoticeImage::getImagePath)
                        .toList())
                .viewCount(notice.getViewCount())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}

