package com.messageapp.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JWT 토큰 응답 DTO
 *
 * <p>OAuth 로그인 및 회원가입 완료 시 반환되는 토큰 정보입니다.</p>
 *
 * <h3>응답 시나리오:</h3>
 * <ul>
 *   <li><b>기존 회원 로그인</b>: isNewMember=false, 모든 회원 정보 포함</li>
 *   <li><b>신규 회원 로그인</b>: isNewMember=true, memberId와 프로필 정보는 null</li>
 *   <li><b>회원가입 완료</b>: isNewMember=false, 모든 회원 정보 포함</li>
 * </ul>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenResponse {

    /** JWT Access Token */
    private String accessToken;

    /** 토큰 타입 (Bearer) */
    private String tokenType;

    /** 토큰 만료 시간 (초 단위) */
    private Long expiresIn;

    /** 회원 ID (신규 회원은 null) */
    private Long memberId;

    /** 회원 이메일 */
    private String email;

    /** 신규 회원 여부 (true면 회원가입 필요) */
    private Boolean isNewMember;

    /** 닉네임 (신규 회원은 null) */
    private String nickname;

    /** 섬 이름 (신규 회원은 null) */
    private String islandName;

    /** 프로필 이미지 인덱스 (신규 회원은 null) */
    private Integer profileImageIndex;
}
