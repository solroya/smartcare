package com.nado.smartcare.member.service.impl;

import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.domain.dto.MemberDto;
import com.nado.smartcare.member.repository.MemberRepository;
import com.nado.smartcare.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    public List<MemberDto> searchByName(String memberName) {
        return memberRepository.findByMemberName(memberName).stream()
                .map(member -> new MemberDto(member.memberNo(), member.memberId(), member.memberPass(), member.memberName(), member.memberEmail(), member.memberGender(), member.memberPhoneNumber(), member.memberBirthday(), member.isSocial(), member.createdAt(), member.updatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MemberDto> findByMemberNameContaining(String memberName, Pageable pageable) {
        return memberRepository.findByMemberNameContaining(memberName, pageable)
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
    	String encodedPassword = passwordEncoder.encode(memberDto.memberPass());
    	
        Member member = Member.of(
                memberDto.memberId(),
                encodedPassword,
                memberDto.memberName(),
                memberDto.memberEmail(),
                memberDto.memberGender(),
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
                .map(member -> new MemberDto(member.memberNo(), member.memberId(), member.memberPass(), member.memberName(), member.memberEmail(), member.memberGender(), member.memberPhoneNumber(), member.memberBirthday(), member.isSocial(), member.createdAt(), member.updatedAt()))
                .toList();
    }
    
    // 로그인
    @Override
    public Member login(String memberId, String memberPass) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id or password"));

        if (!memberPass.equals(member.getMemberPass())) {
            log.info("비밀번호 불일치: 입력된 비밀번호 ==> {}", memberPass);
            throw new IllegalArgumentException("Invalid email or password");
        }

        log.info("로그인 성공: 회원 이름 ==> {}", member.getMemberName());
        return member;
    }
	

    @Override
    public Page<MemberDto> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberDto::from);
    }
    
    
    // 아이디 찾기
    @Override
    public List<MemberDto> findByPhoneNumber(String memberPhoneNumber) {
        return memberRepository.findByMemberPhoneNumber(memberPhoneNumber)
                .stream()
                .map(MemberDto::from)
                .toList();
    }
    
    // 비밀번호 찾기
	@Override
	public boolean verifyMember(String memberId, String memberPhoneNumber) {
		String normalizedPhone = memberPhoneNumber.replaceAll("-", "");
		return memberRepository.findByMemberIdAndMemberPhoneNumber(memberId, memberPhoneNumber).isPresent();
	}

	@Override
	public void updatePassword(String memberId, String newPassword) {
		Member member = memberRepository.findByMemberId(memberId)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
		
		member.setMemberPass(passwordEncoder.encode(newPassword));
		memberRepository.save(member);
	}

}
