package com.nado.smartcare.member.service;

import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.domain.dto.MemberDto;
import com.nado.smartcare.member.domain.dto.SearchMemberDto;
import com.nado.smartcare.page.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberService {

    Optional<MemberDto> searchMemberNo(Long memberId);

    Optional<MemberDto> searchMember(String memberId);

    List<MemberDto> searchByName(String memberName);

    Optional<MemberDto> searchMemberEmail(String memberEmail);

    MemberDto saveMember(MemberDto memberDto);

    PageResponse<SearchMemberDto> findByNameAndMemberBirthday(String memberName, LocalDate memberBirthday, Pageable pageable);
    
    Member login(String memberId, String memberPass);

    PageResponse<SearchMemberDto> getAllMembers(Pageable pageable);
    
    List<MemberDto> findByPhoneNumber(String memberPhoneNumber);
    
    boolean verifyMember(String memberId, String memberPhoneNumber);
    
    void updatePassword(String memberId, String newPassword);
}
