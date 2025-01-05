package com.nado.smartcare.notice.domain;

import com.nado.smartcare.config.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class NoticeAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "notice_answer_seq")
    private Long NoticeAnswerNo;

    @Column
    private String content;

    @ManyToOne
    private Notice notice;

}
