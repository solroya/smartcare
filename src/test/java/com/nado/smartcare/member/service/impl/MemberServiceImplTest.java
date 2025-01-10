package com.nado.smartcare.member.service.impl;

import com.nado.smartcare.member.domain.Member;
import com.nado.smartcare.member.domain.dto.MemberDto;
import com.nado.smartcare.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("비지니스 로직 - 외부 사용자")
@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl sut;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("[비지니스 로직-외부 사용자] 존재 하는 외부 사용자 ID를 검색하면, Member 데이터를 Optional로 반환한다.")
    @Test
    void givenExistentMemberId_whenSearching_thenReturnsOptionalMemberData() {
        // Given
        String memberId = "ChulHee";
        Member mocMember = createMemberAccount(memberId);
        BDDMockito.given(memberRepository.findByMemberId(memberId))
                .willReturn(Optional.of(mocMember));

        // When
        Optional<MemberDto> result = sut.searchMember(memberId);

        // Then
        assertThat(result).isPresent();
        then(memberRepository).should().findByMemberId(memberId);
    }

    @DisplayName("[비지니스 로직-외부 사용자] 존재하지 않는 외부 사용자 ID를 검색하면, 비어있는 Optional 을 반환한다.")
    @Test
    void givenNoneExistentMemberId_whenSearching_thenReturnsEmptyOptional() {
        // Given
        String memberId = "Wrong-User";
        given(memberRepository.findByMemberId(memberId)).willReturn(Optional.empty());

        // When
        Optional<MemberDto> result = sut.searchMember(memberId);

        // Then
        assertThat(result).isEmpty();
        then(memberRepository).should().findByMemberId(memberId);
    }

    @DisplayName("[비지니스 로직-외부 사용자] 외부 사용자 정보를 입력하면, 새로운 외부 사용자 회원 정보를 저장하여 가입하고, 해당 사용자 데이터를 반환한다.")
    @Test
    void givenMemberParams_WhenSaving_thenReturnsMember() {
        // Given
        String memberId = "newUser123";
        String memberPass = "password123";
        String memberName = "New User";
        String memberEmail = "newuser@test.com";
        String memberPhoneNumber = "010-1234-1234";
        LocalDate memberBirthday = LocalDate.of(1988, 1, 7);
        boolean isSocial = false;

        Member mocMember = new Member(
                memberId,
                memberPass,
                memberName,
                memberEmail,
                memberPhoneNumber,
                memberBirthday,
                isSocial
        );
        given(memberRepository.save(any(Member.class))).willReturn(mocMember);

        // When
        MemberDto result = sut.saveMember(
                MemberDto.from(mocMember)
        );

        // Then
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        then(memberRepository).should().save(captor.capture());
        Member capturedMember = captor.getValue();

        // Verify saved member properties
        assertThat(capturedMember.getMemberId()).isEqualTo(memberId);
        assertThat(capturedMember.getMemberName()).isEqualTo(memberName);

        // Verify returned result
        assertThat(result).isNotNull();
        assertThat(result.memberId()).isEqualTo(memberId);
        assertThat(result.memberName()).isEqualTo(memberName);
    }

    @DisplayName("[비지니스 로직-외부 사용자] 회원정보를 수정하면, 수정된 정보를 반환한다.")
    @Test
    void givenMemberIdAndUpdatedMemberInfo_whenUpdating_thenReturnsMember() {
        // Given
        Long no = 1L;
        String newMemberPass = "password123";
        String newMemberPhoneNumber = "010-1234-1234";

        Member existingMember = Member.of(
                "user123",
                "password123",
                "Old Name",
                "old@test.com",
                "010-1234-5678",
                LocalDate.of(1988, 1, 7),
                false
        );

        given(memberRepository.findByNo(no)).willReturn(Optional.of(existingMember));
        given(memberRepository.save(any(Member.class))).willReturn(existingMember);

        // When
        MemberDto result = sut.updateMember(no, newMemberPass, newMemberPhoneNumber);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.memberPass()).isEqualTo(existingMember.getMemberPass());
        assertThat(result.memberPhoneNumber()).isEqualTo(existingMember.getMemberPhoneNumber());

        then(memberRepository).should().findByNo(no);
        then(memberRepository).should().save(any(Member.class));
    }

    @DisplayName("[비지니스 로직-외부 사용자] 회원 정보를 삭제하면, 저장소에서 해당 데이터가 제거 된다.")
    @Test
    void givenMemberNo_whenDeleting_thenReturnsMemberIsDeleted() {
        // Given
        Long no = 1L;

        Member existingMember = Member.of(
                "user123",
                "password123",
                "User Name",
                "user@test.com",
                "010-1234-5678",
                LocalDate.of(1988, 1, 7),
                false
        );
        given(memberRepository.findByNo(no)).willReturn(Optional.of(existingMember));

        // When
        sut.deleteMember(no);

        // Then
        then(memberRepository).should().findByNo(no);
        then(memberRepository).should().delete(existingMember);
    }

    @DisplayName("[비지니스 로직-외부 사용자] 존재하지 않는 회원을 삭제하려 하면, 예외가 발생된다")
    @Test
    void givenNonExistentMemberId_whenDeleting_thenThrowsException() {
        // Given
        Long no = 999L;
        given(memberRepository.findByNo(no)).willReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> sut.deleteMember(no));
        then(memberRepository).should().findByNo(no);
        then(memberRepository).shouldHaveNoMoreInteractions();
    }


    private Member createMemberAccount(String memberId) {
        return new Member(
                memberId,              // memberId
                "password123",         // memberPass
                "ChulHee",             // memberName
                memberId + "@test.com",// memberEmail
                "010-1234-1234",       // memberPhoneNumber
                LocalDate.of(1988, 1, 7), // memberBirthday
                false                  // isSocial
        );
    }
}