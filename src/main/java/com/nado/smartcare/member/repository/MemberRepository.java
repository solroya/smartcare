package com.nado.smartcare.member.repository;

import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.domain.dto.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberNo(Long memberNo);

    Optional<Member> findByMemberId(String memberId);

    List<MemberDto> findByMemberName(String memberName);

    Optional<Member> findByMemberEmail(String memberEmail);

    List<MemberDto> findByMemberNameAndMemberBirthday(String memberName, LocalDate memberBirthday);
    
    List<Member> findByMemberPhoneNumber(String phoneNumber);
    
    Optional<Member> findByMemberIdAndMemberPhoneNumber(String memberId, String memberPhoneNumber);

    Page<Member> findByMemberNameContaining(String memberName, Pageable pageable);

}
