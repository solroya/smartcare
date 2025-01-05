package com.nado.smartcare.notice.domain;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Entity
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "notice_seq")
    private Long noticeNo;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(nullable = false, length = 1000)
    private String content;

    @Column
    private String image;

    @Column
    private Long viewCount;

    @Setter
    @JoinColumn(name = "employee_no")
    @ManyToOne
    private Employee employee;


    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<NoticeImage> images = new ArrayList<>();


}
