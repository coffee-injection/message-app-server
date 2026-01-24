package com.messageapp.domain.auth.service;

import com.messageapp.api.auth.OauthProvider;
import com.messageapp.domain.auth.strategy.OauthUnlinkStrategy;
import com.messageapp.global.exception.auth.UnsupportedOauthProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OAuth 연결 해제 서비스
 * Strategy 패턴을 통해 각 OAuth 제공자별 연결 해제 로직을 위임
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OauthUnlinkService {

    private final List<OauthUnlinkStrategy> strategies;

    /**
     * OAuth 연결 해제 수행
     * @param provider OAuth 제공자
     * @param oauthId OAuth 사용자 ID
     */
    public void unlink(OauthProvider provider, String oauthId) {
        OauthUnlinkStrategy strategy = strategies.stream()
                .filter(s -> s.supports(provider))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("지원하지 않는 OAuth 제공자: {}", provider);
                    return new UnsupportedOauthProviderException();
                });

        strategy.unlink(oauthId);
    }
}
