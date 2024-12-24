package com.nado.smartcare.member.domain;

import com.nado.smartcare.config.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long no;

    private String memberId;

    private String memberPass;

    private String memberName;

    private String memberEmail;

    private String memberPhoneNumber;

    private LocalDateTime memberBirthday;

    private boolean isSocial;
}
