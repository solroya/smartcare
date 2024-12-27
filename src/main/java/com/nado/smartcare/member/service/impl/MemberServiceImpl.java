package com.nado.smartcare.member.service.impl;

import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.dto.MemberDto;
import com.nado.smartcare.member.repository.MemberRepository;
import com.nado.smartcare.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Optional<MemberDto> searchMember(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .map(MemberDto::from);
    }

    @Transactional(readOnly = true)
    public Optional<MemberDto> searchMemberEmail(String memberEmail) {
        return memberRepository.findByMemberEmail(memberEmail)
                .map(MemberDto::from);
    }

    public MemberDto saveMember(String memberId, String memberPass, String memberName, String memberEmail,
                                String memberPhoneNumber, LocalDateTime memberBirthDay, boolean isSocial) {
        return MemberDto.from(
                memberRepository.save(Member.of(memberId, memberPass, memberName, memberEmail, memberPhoneNumber, memberBirthDay, isSocial))
        );
    }

    @Transactional
    public MemberDto updateMember(Long no, String newMemberPass, String newMemberPhoneNumber) {
        Member member = memberRepository.findByNo(no)
                .orElseThrow(() -> new IllegalArgumentException("No member found with id: " + no));

        member.updateMember(newMemberPass, newMemberPhoneNumber);
        Member updatedMember = memberRepository.save(member);
        return MemberDto.from(updatedMember);
    }

    @Transactional
    public void deleteMember(Long no) {
        Member member = memberRepository.findByNo(no)
                .orElseThrow(() -> new IllegalArgumentException("No member found with id: " + no));
        memberRepository.delete(member);
    }
}
