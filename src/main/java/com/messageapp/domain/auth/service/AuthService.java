package com.messageapp.domain.auth.service;

import com.messageapp.domain.auth.dto.JwtTokenResponse;
import com.messageapp.global.exception.auth.InvalidAccessTokenException;
import com.messageapp.global.exception.business.member.DuplicateMemberException;
import com.messageapp.global.exception.business.member.MemberNotFoundException;
import com.messageapp.global.exception.validation.InvalidTempTokenException;

/**
 * 인증 서비스 인터페이스
 *
 * <p>OAuth 소셜 로그인, 회원가입 완료, 토큰 갱신, 회원 탈퇴 기능을 정의합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
public interface AuthService {

    /**
     * 카카오 소셜 로그인을 수행합니다.
     *
     * @param authorizationCode 카카오 인가 코드
     * @return JWT 토큰 응답 (기존 회원: 정식 토큰, 신규 회원: 임시 토큰)
     */
    JwtTokenResponse kakaoLogin(String authorizationCode);

    /**
     * 구글 소셜 로그인을 수행합니다.
     *
     * @param authorizationCode 구글 인가 코드 (URL 인코딩된 상태)
     * @return JWT 토큰 응답 (기존 회원: 정식 토큰, 신규 회원: 임시 토큰)
     */
    JwtTokenResponse googleLogin(String authorizationCode);

    /**
     * 애플 소셜 로그인을 수행합니다.
     *
     * @param idToken Apple에서 발급한 idToken (JWT)
     * @return JWT 토큰 응답 (기존 회원: 정식 토큰, 신규 회원: 임시 토큰)
     */
    JwtTokenResponse appleLogin(String idToken);

    /**
     * 회원가입을 완료합니다.
     *
     * <p>소셜 로그인 후 발급받은 임시 토큰과 함께 닉네임, 섬 이름,
     * 프로필 이미지 인덱스를 입력하여 회원가입을 완료합니다.</p>
     *
     * @param token 임시 JWT 토큰
     * @param nickname 닉네임 (2~10자)
     * @param islandName 섬 이름 (1~8자)
     * @param profileImageIndex 프로필 이미지 인덱스 (1~21)
     * @return 정식 JWT 토큰 응답
     * @throws InvalidAccessTokenException 토큰이 유효하지 않은 경우
     * @throws InvalidTempTokenException 임시 토큰이 아닌 경우
     * @throws DuplicateMemberException 이미 가입된 이메일인 경우
     */
    JwtTokenResponse completeSignup(String token, String nickname, String islandName, Integer profileImageIndex);

    /**
     * Refresh Token으로 Access Token을 갱신합니다.
     *
     * <p>유효한 Refresh Token을 제출하면 새로운 Access Token과
     * Refresh Token(Rotation)을 발급합니다.</p>
     *
     * @param refreshToken Refresh Token
     * @return 새로운 JWT 토큰 응답
     */
    JwtTokenResponse refreshToken(String refreshToken);

    /**
     * 회원 탈퇴를 처리합니다.
     *
     * <p>OAuth 연결을 해제하고 회원 상태를 INACTIVE로 변경합니다.
     * Refresh Token도 함께 삭제됩니다.</p>
     *
     * @param memberId 탈퇴할 회원 ID
     * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
     */
    void withdrawMember(Long memberId);
}
