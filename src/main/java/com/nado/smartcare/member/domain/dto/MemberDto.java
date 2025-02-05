package com.nado.smartcare.member.domain.dto;

import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MemberDto(
        Long memberNo,

        @NotBlank(message = "Member Id is Required")
        String memberId,

        @NotBlank(message = "Password is required.")
        String memberPass,

        @NotBlank(message = "Name is required.")
        String memberName,

        @Email(message = "Email must be valid.")
        String memberEmail,
        
        @NotNull(message = "Gender is required.")
        boolean memberGender,

        @Pattern(regexp = "\\d{3}-\\d{3,4}-\\d{4}", message = "Phone number must follow the format 010-1234-5678.")
        String memberPhoneNumber,

        @NotNull(message = "Birthday is required.")
        LocalDate memberBirthday,

        boolean isSocial,

        List<PatientRecordCard> patientRecordCards,

        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static MemberDto from(Member member) {
        return new MemberDto(
                member.getMemberNo(),
                member.getMemberId(),
                member.getMemberPass(),
                member.getMemberName(),
                member.getMemberEmail(),
                member.isMemberGender(),
                member.getMemberPhoneNumber(),
                member.getMemberBirthday(),
                member.isSocial(),
                member.getPatientRecordCards(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
