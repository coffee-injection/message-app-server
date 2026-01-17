package com.messageapp.domain.auth.strategy;

import com.messageapp.api.auth.OauthProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 구글 소셜 로그인 연결 해제 전략
 * 구글은 서버 사이드에서 별도의 연결 해제 API 호출이 불필요함
 */
@Slf4j
@Component
public class GoogleUnlinkStrategy implements OauthUnlinkStrategy {

    @Override
    public boolean supports(OauthProvider provider) {
        return provider == OauthProvider.GOOGLE;
    }

    @Override
    public void unlink(String oauthId) {
        // 구글은 서버사이드 연결 해제 API가 별도로 필요하지 않음
        // 클라이언트 측에서 Google Sign-In SDK를 통해 처리하거나,
        // 사용자가 직접 Google 계정 설정에서 앱 연결을 해제함
        log.info("구글 계정 연결 해제 처리 (별도 API 호출 없음): oauthId = {}", oauthId);
    }
}
