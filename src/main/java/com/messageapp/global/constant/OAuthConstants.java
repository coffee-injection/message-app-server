package com.messageapp.global.constant;

/**
 * OAuth 인증 관련 상수 정의 클래스
 *
 * <p>OAuth 2.0 인증 플로우에서 사용되는 상수들을 정의합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
public final class OAuthConstants {

    /**
     * 인스턴스 생성 방지를 위한 private 생성자
     */
    private OAuthConstants() {
    }

    // ===== Grant Type =====

    /** OAuth 2.0 Authorization Code Grant 타입 */
    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";

    // ===== Token Type =====

    /** Bearer 토큰 타입 (Authorization 헤더에 사용) */
    public static final String TOKEN_TYPE_BEARER = "Bearer";

    /** 임시 토큰 타입 (회원가입 전 신규 사용자용) */
    public static final String TOKEN_TYPE_TEMP = "temp";
}
