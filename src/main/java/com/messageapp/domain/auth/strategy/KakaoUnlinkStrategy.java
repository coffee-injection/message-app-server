package com.messageapp.domain.auth.strategy;

import com.messageapp.api.auth.OauthProvider;
import com.messageapp.domain.auth.client.KakaoApiClient;
import com.messageapp.global.exception.external.KakaoUnlinkFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 카카오 소셜 로그인 연결 해제 전략
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoUnlinkStrategy implements OauthUnlinkStrategy {

    private final KakaoApiClient kakaoApiClient;

    @Value("${oauth.kakao.admin-key}")
    private String kakaoAdminKey;

    @Override
    public boolean supports(OauthProvider provider) {
        return provider == OauthProvider.KAKAO;
    }

    @Override
    public void unlink(String oauthId) {
        try {
            kakaoApiClient.unlinkUser(
                    "KakaoAK " + kakaoAdminKey,
                    "user_id",
                    oauthId
            );
            log.info("카카오 연결 끊기 성공: oauthId = {}", oauthId);
        } catch (Exception e) {
            log.error("카카오 연결 끊기 실패: oauthId = {}, error = {}", oauthId, e.getMessage());
            throw KakaoUnlinkFailedException.EXCEPTION;
        }
    }
}
