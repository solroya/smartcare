package com.nado.smartcare.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nado.smartcare.config.BaseEntity;
import com.nado.smartcare.member.domain.dto.MemberDto;
import com.nado.smartcare.patient.domain.PatientRecordCard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
@Entity
@Setter
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "member_seq")
    private Long memberNo;

    @Column(nullable = false, unique = true)
    private String memberId;

    private String memberPass;

    private String memberName;

    @Column(nullable = false, unique = true)
    private String memberEmail;

    private boolean memberGender;
    
    private String memberPhoneNumber;

    private LocalDate memberBirthday;

    private boolean isSocial;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<PatientRecordCard> patientRecordCards;


    public Member() {
    }

    public Member(String memberId, String memberPass, String memberName, String memberEmail, boolean memberGender, String memberPhoneNumber, LocalDate memberBirthday, boolean isSocial, List<PatientRecordCard> patientRecordCards) {
        this.memberId = memberId;
        this.memberPass = memberPass;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberGender = memberGender;
        this.memberPhoneNumber = memberPhoneNumber;
        this.memberBirthday = memberBirthday;
        this.isSocial = isSocial;
        this.patientRecordCards = patientRecordCards;
    }


    public static Member of(String memberId, String memberPass, String memberName, String memberEmail, boolean memberGender,
                            String memberPhoneNumber, LocalDate memberBirthDay, boolean isSocial, List<PatientRecordCard> patientRecordCards) {
        return new Member(memberId, memberPass, memberName, memberEmail, memberGender, memberPhoneNumber, memberBirthDay, isSocial, patientRecordCards);
    }

    public Member updateMember(String newMemberPass, String newMemberPhoneNumber) {
        if (newMemberPass != null && !newMemberPass.isBlank()) {
            this.memberPass = newMemberPass;
        }
        if (newMemberPhoneNumber != null && !newMemberPhoneNumber.isBlank()) {
            this.memberPhoneNumber = newMemberPhoneNumber;
        }
        return this; // 체이닝
    }

    public static Member toEntity(MemberDto dto) {
        return new Member(
                dto.memberId(),
                dto.memberPass(),
                dto.memberName(),
                dto.memberEmail(),
                dto.memberGender(),
                dto.memberPhoneNumber(),
                dto.memberBirthday(),
                dto.isSocial(),
                dto.patientRecordCards()
        );
    }
}
