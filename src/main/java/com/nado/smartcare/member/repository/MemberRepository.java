package com.nado.smartcare.member.repository;

import com.nado.smartcare.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
