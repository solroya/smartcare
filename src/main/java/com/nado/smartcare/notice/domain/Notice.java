package com.nado.smartcare.notice.domain;

import com.nado.smartcare.employee.domain.Employee;
import com.nado.smartcare.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

    @Setter
    @JoinColumn(name = "employee_no")
    @ManyToOne
    private Employee employee;
}
