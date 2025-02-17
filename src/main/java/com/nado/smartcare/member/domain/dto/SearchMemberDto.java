package com.nado.smartcare.member.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class SearchMemberDto {
    private Long memberNo;
    private String memberId;
    private String memberPass;
    private String memberName;
    private String memberEmail;
    private boolean memberGender;
    private String memberPhoneNumber;
    private LocalDate memberBirthday;
    private boolean isSocial;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
