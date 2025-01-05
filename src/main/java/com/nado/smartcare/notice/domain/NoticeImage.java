package com.nado.smartcare.notice.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class NoticeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "notice_image_seq")
    private Long noticeImageId;

    private String imagePath;

    private String thumbnailPath;

    @ManyToOne(fetch = FetchType.LAZY)
    private Notice notice;
}
