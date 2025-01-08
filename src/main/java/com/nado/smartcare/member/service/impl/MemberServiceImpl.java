package com.nado.smartcare.member.service.impl;

import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.dto.MemberDto;
import com.nado.smartcare.member.repository.MemberRepository;
import com.nado.smartcare.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Optional<MemberDto> searchMemberNo(Long memberId) {
        return memberRepository.findByMemberNo(memberId)
                .map(MemberDto::from);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<MemberDto> searchMember(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .map(MemberDto::from);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<MemberDto> searchMemberEmail(String memberEmail) {
        return memberRepository.findByMemberEmail(memberEmail)
                .map(MemberDto::from);
    }

    @Override
    public MemberDto saveMember(MemberDto memberDto) {
        Member member = Member.of(
                memberDto.memberId(),
                memberDto.memberPass(),
                memberDto.memberName(),
                memberDto.memberEmail(),
                memberDto.memberPhoneNumber(),
                memberDto.memberBirthday(),
                memberDto.isSocial()
        );

        return MemberDto.from(memberRepository.save(member));
    }

    @Transactional
    @Override
    public MemberDto updateMember(Long memberNo, String newMemberPass, String newMemberPhoneNumber) {
        Member member = memberRepository.findByMemberNo(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("No member found with id: " + memberNo));

        member.updateMember(newMemberPass, newMemberPhoneNumber);
        Member updatedMember = memberRepository.save(member);
        return MemberDto.from(updatedMember);
    }

    @Transactional
    @Override
    public void deleteMember(Long memberNo) {
        Member member = memberRepository.findByMemberNo(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("No member found with id: " + memberNo));
        memberRepository.delete(member);
    }

    @Override
    public List<MemberDto> findByNameAndMemberBirthday(String memberName, LocalDate memberBirthday) {
        return memberRepository.findByMemberNameAndMemberBirthday(memberName, memberBirthday).stream()
                .map(member -> new MemberDto(member.memberNo(), member.memberId(), member.memberPass(), member.memberName(), member.memberEmail(), member.memberPhoneNumber(), member.memberBirthday(), member.isSocial(), member.createdAt(), member.updatedAt()))
                .toList();
    }
}
