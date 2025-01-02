package com.nado.smartcare.member.service;

import com.nado.smartcare.member.dto.MemberDto;

import java.time.LocalDate;
import java.util.Optional;

public interface MemberService {

    Optional<MemberDto> searchMember(String memberId);

    Optional<MemberDto> searchMemberEmail(String memberEmail);

    MemberDto saveMember(MemberDto memberDto);

    MemberDto updateMember(Long no, String newMemberPass, String newMemberPhoneNumber);

    void deleteMember(Long no);
}
