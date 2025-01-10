package com.nado.smartcare.member.domain;

import com.nado.smartcare.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@Entity
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

    public Member() {
    }

    public Member(String memberId, String memberPass, String memberName, String memberEmail, boolean memberGender, String memberPhoneNumber, LocalDate memberBirthday, boolean isSocial) {
        this.memberId = memberId;
        this.memberPass = memberPass;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberGender = memberGender;
        this.memberPhoneNumber = memberPhoneNumber;
        this.memberBirthday = memberBirthday;
        this.isSocial = isSocial;
    }

    public static Member of(String memberId, String memberPass, String memberName, String memberEmail, boolean memberGender,
                            String memberPhoneNumber, LocalDate memberBirthDay, boolean isSocial) {
        return new Member(memberId, memberPass, memberName, memberEmail, memberGender, memberPhoneNumber, memberBirthDay, isSocial);
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
}
