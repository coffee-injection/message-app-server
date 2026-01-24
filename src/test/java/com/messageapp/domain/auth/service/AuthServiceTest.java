package com.messageapp.domain.auth.service;

import com.messageapp.api.auth.OauthProvider;
import com.messageapp.domain.auth.dto.JwtTokenResponse;
import com.messageapp.domain.auth.dto.OAuthUserInfo;
import com.messageapp.domain.auth.strategy.OAuthLoginStrategy;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import com.messageapp.fixture.TestFixture;
import com.messageapp.global.config.JwtProperties;
import com.messageapp.global.exception.auth.InvalidAccessTokenException;
import com.messageapp.global.exception.business.member.DuplicateMemberException;
import com.messageapp.global.exception.business.member.MemberAlreadyWithdrawnException;
import com.messageapp.global.exception.business.member.MemberNotFoundException;
import com.messageapp.global.exception.validation.InvalidTempTokenException;
import com.messageapp.global.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private OauthUnlinkService oauthUnlinkService;

    @Mock
    private OAuthLoginStrategy kakaoLoginStrategy;

    @BeforeEach
    void setUp() {
        given(kakaoLoginStrategy.getProvider()).willReturn(OauthProvider.KAKAO);
        authService = new AuthServiceImpl(
                memberRepository,
                jwtTokenProvider,
                jwtProperties,
                oauthUnlinkService,
                List.of(kakaoLoginStrategy)
        );
        authService.initStrategyMap();
    }

    @Nested
    @DisplayName("kakaoLogin 메서드")
    class KakaoLoginTest {

        @Test
        @DisplayName("기존 회원이면 정식 JWT를 발급한다")
        void existingMember_returnsJwt() {
            // given
            String authCode = "auth-code";
            OAuthUserInfo userInfo = TestFixture.createKakaoUserInfo();
            Member member = TestFixture.createActiveMember(1L);

            given(kakaoLoginStrategy.authenticate(authCode)).willReturn(userInfo);
            given(memberRepository.findByEmail(userInfo.getEmail())).willReturn(Optional.of(member));
            given(jwtTokenProvider.generateAccessToken(member.getId(), member.getEmail()))
                    .willReturn("access-token");
            given(jwtProperties.getAccessTokenExpiration()).willReturn(86400000L);

            // when
            JwtTokenResponse response = authService.kakaoLogin(authCode);

            // then
            assertThat(response.getAccessToken()).isEqualTo("access-token");
            assertThat(response.getIsNewMember()).isFalse();
            assertThat(response.getMemberId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("신규 회원이면 임시 JWT를 발급한다")
        void newMember_returnsTempJwt() {
            // given
            String authCode = "auth-code";
            OAuthUserInfo userInfo = TestFixture.createKakaoUserInfo();

            given(kakaoLoginStrategy.authenticate(authCode)).willReturn(userInfo);
            given(memberRepository.findByEmail(userInfo.getEmail())).willReturn(Optional.empty());
            given(jwtTokenProvider.generateTempAccessToken(userInfo.getOauthId(), userInfo.getEmail(), OauthProvider.KAKAO))
                    .willReturn("temp-token");
            given(jwtProperties.getAccessTokenExpiration()).willReturn(86400000L);

            // when
            JwtTokenResponse response = authService.kakaoLogin(authCode);

            // then
            assertThat(response.getAccessToken()).isEqualTo("temp-token");
            assertThat(response.getIsNewMember()).isTrue();
            assertThat(response.getMemberId()).isNull();
        }
    }

    @Nested
    @DisplayName("completeSignup 메서드")
    class CompleteSignupTest {

        @Test
        @DisplayName("유효한 임시 토큰으로 회원가입을 완료한다")
        void validTempToken_completesSignup() {
            // given
            String tempToken = "temp-token";
            String nickname = "새회원";
            String islandName = "새섬";
            Integer profileImageIndex = 5;

            given(jwtTokenProvider.validateToken(tempToken)).willReturn(true);
            given(jwtTokenProvider.getTokenType(tempToken)).willReturn("temp");
            given(jwtTokenProvider.getOauthIdFromToken(tempToken)).willReturn("oauth123");
            given(jwtTokenProvider.getOauthProviderFromToken(tempToken)).willReturn(OauthProvider.KAKAO);
            given(jwtTokenProvider.getEmailFromToken(tempToken)).willReturn("test@test.com");
            given(memberRepository.findByEmail("test@test.com")).willReturn(Optional.empty());
            given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
                Member m = invocation.getArgument(0);
                try {
                    java.lang.reflect.Field idField = Member.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(m, 1L);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return m;
            });
            given(jwtTokenProvider.generateAccessToken(1L, "test@test.com")).willReturn("access-token");
            given(jwtProperties.getAccessTokenExpiration()).willReturn(86400000L);

            // when
            JwtTokenResponse response = authService.completeSignup(tempToken, nickname, islandName, profileImageIndex);

            // then
            assertThat(response.getAccessToken()).isEqualTo("access-token");
            assertThat(response.getIsNewMember()).isFalse();
            assertThat(response.getNickname()).isEqualTo(nickname);
            verify(memberRepository).save(any(Member.class));
        }

        @Test
        @DisplayName("유효하지 않은 토큰이면 예외를 발생시킨다")
        void invalidToken_throwsException() {
            // given
            given(jwtTokenProvider.validateToken("invalid")).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.completeSignup("invalid", "nick", "island", 1))
                    .isInstanceOf(InvalidAccessTokenException.class);
        }

        @Test
        @DisplayName("임시 토큰이 아니면 예외를 발생시킨다")
        void notTempToken_throwsException() {
            // given
            given(jwtTokenProvider.validateToken("token")).willReturn(true);
            given(jwtTokenProvider.getTokenType("token")).willReturn("access");

            // when & then
            assertThatThrownBy(() -> authService.completeSignup("token", "nick", "island", 1))
                    .isInstanceOf(InvalidTempTokenException.class);
        }

        @Test
        @DisplayName("이미 가입된 이메일이면 예외를 발생시킨다")
        void duplicateEmail_throwsException() {
            // given
            given(jwtTokenProvider.validateToken("temp")).willReturn(true);
            given(jwtTokenProvider.getTokenType("temp")).willReturn("temp");
            given(jwtTokenProvider.getEmailFromToken("temp")).willReturn("existing@test.com");
            given(memberRepository.findByEmail("existing@test.com"))
                    .willReturn(Optional.of(TestFixture.createActiveMember(1L)));

            // when & then
            assertThatThrownBy(() -> authService.completeSignup("temp", "nick", "island", 1))
                    .isInstanceOf(DuplicateMemberException.class);
        }
    }

    @Nested
    @DisplayName("withdrawMember 메서드")
    class WithdrawMemberTest {

        @Test
        @DisplayName("회원 탈퇴를 성공적으로 처리한다")
        void validMember_withdrawsSuccessfully() {
            // given
            Member member = TestFixture.createActiveMember(1L);
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));
            doNothing().when(oauthUnlinkService).unlink(any(), any());

            // when
            authService.withdrawMember(1L);

            // then
            assertThat(member.isActive()).isFalse();
            verify(oauthUnlinkService).unlink(OauthProvider.KAKAO, member.getOauthId());
        }

        @Test
        @DisplayName("존재하지 않는 회원이면 예외를 발생시킨다")
        void nonExistentMember_throwsException() {
            // given
            given(memberRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.withdrawMember(999L))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("이미 탈퇴한 회원이면 예외를 발생시킨다")
        void alreadyWithdrawnMember_throwsException() {
            // given
            Member member = TestFixture.createActiveMember(1L);
            member.deactivate();
            given(memberRepository.findById(1L)).willReturn(Optional.of(member));

            // when & then
            assertThatThrownBy(() -> authService.withdrawMember(1L))
                    .isInstanceOf(MemberAlreadyWithdrawnException.class);
        }
    }
}
