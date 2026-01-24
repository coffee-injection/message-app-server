package com.messageapp.domain.member.service;

import com.messageapp.domain.member.dto.CheckNicknameResponse;
import com.messageapp.domain.member.dto.UpdateProfileRequest;
import com.messageapp.domain.member.dto.UpdateProfileResponse;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import com.messageapp.fixture.TestFixture;
import com.messageapp.global.exception.business.member.DuplicateNicknameException;
import com.messageapp.global.exception.business.member.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    private UpdateProfileRequest createRequest(String nickname, String islandName, Integer profileImageIndex) {
        UpdateProfileRequest request = new UpdateProfileRequest();
        try {
            if (nickname != null) {
                Field nicknameField = UpdateProfileRequest.class.getDeclaredField("nickname");
                nicknameField.setAccessible(true);
                nicknameField.set(request, nickname);
            }
            if (islandName != null) {
                Field islandNameField = UpdateProfileRequest.class.getDeclaredField("islandName");
                islandNameField.setAccessible(true);
                islandNameField.set(request, islandName);
            }
            if (profileImageIndex != null) {
                Field indexField = UpdateProfileRequest.class.getDeclaredField("profileImageIndex");
                indexField.setAccessible(true);
                indexField.set(request, profileImageIndex);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    @Nested
    @DisplayName("checkNickname 메서드")
    class CheckNicknameTest {

        @Test
        @DisplayName("사용 가능한 닉네임이면 true를 반환한다")
        void availableNickname_returnsTrue() {
            // given
            String nickname = "사용가능닉네임";
            given(memberRepository.existsByName(nickname)).willReturn(false);

            // when
            CheckNicknameResponse response = memberService.checkNickname(nickname);

            // then
            assertThat(response.isAvailable()).isTrue();
        }

        @Test
        @DisplayName("이미 사용 중인 닉네임이면 false를 반환한다")
        void duplicateNickname_returnsFalse() {
            // given
            String nickname = "중복닉네임";
            given(memberRepository.existsByName(nickname)).willReturn(true);

            // when
            CheckNicknameResponse response = memberService.checkNickname(nickname);

            // then
            assertThat(response.isAvailable()).isFalse();
        }
    }

    @Nested
    @DisplayName("updateProfile 메서드")
    class UpdateProfileTest {

        @Test
        @DisplayName("닉네임을 성공적으로 변경한다")
        void updateNickname_success() {
            // given
            Long memberId = 1L;
            Member member = TestFixture.createActiveMember(memberId);
            String newNickname = "새닉네임";

            UpdateProfileRequest request = createRequest(newNickname, null, null);

            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(memberRepository.existsByName(newNickname)).willReturn(false);

            // when
            UpdateProfileResponse response = memberService.updateProfile(memberId, request);

            // then
            assertThat(member.getName()).isEqualTo(newNickname);
        }

        @Test
        @DisplayName("같은 닉네임으로 변경 시 중복 체크를 하지 않는다")
        void sameNickname_skipsDuplicateCheck() {
            // given
            Long memberId = 1L;
            Member member = TestFixture.createMember(memberId, "test@test.com", "현재닉네임");

            UpdateProfileRequest request = createRequest("현재닉네임", null, null);

            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

            // when
            memberService.updateProfile(memberId, request);

            // then - existsByName이 호출되지 않음
            org.mockito.Mockito.verify(memberRepository, org.mockito.Mockito.never()).existsByName("현재닉네임");
        }

        @Test
        @DisplayName("중복된 닉네임으로 변경 시 예외가 발생한다")
        void duplicateNickname_throwsException() {
            // given
            Long memberId = 1L;
            Member member = TestFixture.createActiveMember(memberId);
            String duplicateNickname = "중복닉네임";

            UpdateProfileRequest request = createRequest(duplicateNickname, null, null);

            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(memberRepository.existsByName(duplicateNickname)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> memberService.updateProfile(memberId, request))
                    .isInstanceOf(DuplicateNicknameException.class);
        }

        @Test
        @DisplayName("섬 이름을 성공적으로 변경한다")
        void updateIslandName_success() {
            // given
            Long memberId = 1L;
            Member member = TestFixture.createActiveMember(memberId);
            String newIslandName = "새섬이름";

            UpdateProfileRequest request = createRequest(null, newIslandName, null);

            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

            // when
            memberService.updateProfile(memberId, request);

            // then
            assertThat(member.getIslandName()).isEqualTo(newIslandName);
        }

        @Test
        @DisplayName("프로필 이미지 인덱스를 성공적으로 변경한다")
        void updateProfileImageIndex_success() {
            // given
            Long memberId = 1L;
            Member member = TestFixture.createActiveMember(memberId);
            Integer newIndex = 10;

            UpdateProfileRequest request = createRequest(null, null, newIndex);

            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

            // when
            memberService.updateProfile(memberId, request);

            // then
            assertThat(member.getProfileImageIndex()).isEqualTo(newIndex);
        }

        @Test
        @DisplayName("존재하지 않는 회원이면 예외가 발생한다")
        void nonExistentMember_throwsException() {
            // given
            Long memberId = 999L;
            given(memberRepository.findById(memberId)).willReturn(Optional.empty());

            UpdateProfileRequest request = createRequest("새닉네임", null, null);

            // when & then
            assertThatThrownBy(() -> memberService.updateProfile(memberId, request))
                    .isInstanceOf(MemberNotFoundException.class);
        }
    }
}
