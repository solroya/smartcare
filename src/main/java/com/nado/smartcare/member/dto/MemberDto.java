package com.nado.smartcare.member.dto;

import com.nado.smartcare.member.domain.Member;

import java.time.LocalDateTime;

public record MemberDto(
        String memberId,
        String memberPass,
        String memberName,
        String memberEmail,
        String memberPhoneNumber,
        LocalDateTime memberBirthday,
        boolean isSocial,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static MemberDto from(Member member) {
        return new MemberDto(
                member.getMemberId(),
                member.getMemberPass(),
                member.getMemberName(),
                member.getMemberEmail(),
                member.getMemberPhoneNumber(),
                member.getMemberBirthday(),
                member.isSocial(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
