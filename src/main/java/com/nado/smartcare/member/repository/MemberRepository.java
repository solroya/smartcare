package com.nado.smartcare.member.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.dto.MemberDto;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberNo(Long no);

    Optional<Member> findByMemberId(String memberId);

    Optional<Member> findByMemberEmail(String memberEmail);
}
