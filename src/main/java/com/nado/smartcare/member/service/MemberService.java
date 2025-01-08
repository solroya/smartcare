package com.nado.smartcare.member.service;

import com.nado.smartcare.member.dto.MemberDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberService {

    Optional<MemberDto> searchMemberNo(Long memberId);

    Optional<MemberDto> searchMember(String memberId);

    Optional<MemberDto> searchMemberEmail(String memberEmail);

    MemberDto saveMember(MemberDto memberDto);

    MemberDto updateMember(Long no, String newMemberPass, String newMemberPhoneNumber);

    void deleteMember(Long no);

    List<MemberDto> findByNameAndMemberBirthday(String memberName, LocalDate memberBirthday);
}
