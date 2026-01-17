package com.messageapp.domain.auth.strategy;

import com.messageapp.api.auth.OauthProvider;

/**
 * 소셜 로그인 연결 해제 전략 인터페이스
 * Strategy 패턴을 적용하여 각 OAuth 제공자별 연결 해제 로직을 분리
 */
public interface OauthUnlinkStrategy {

    /**
     * 해당 OAuth 제공자를 지원하는지 확인
     * @param provider OAuth 제공자
     * @return 지원 여부
     */
    boolean supports(OauthProvider provider);

    /**
     * 소셜 로그인 연결 해제 수행
     * @param oauthId OAuth 제공자의 사용자 ID
     */
    void unlink(String oauthId);
}
