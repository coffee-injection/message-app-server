package com.messageapp.domain.auth.strategy;

import com.messageapp.api.auth.OauthProvider;
import com.messageapp.domain.auth.dto.OAuthUserInfo;
import com.messageapp.domain.auth.validator.AppleIdTokenValidator;
import com.messageapp.domain.auth.validator.AppleIdTokenValidator.AppleUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Apple 로그인 전략 구현체
 *
 * <p>Apple Sign in with Apple 인증 플로우를 처리합니다.</p>
 *
 * <h3>인증 플로우:</h3>
 * <ol>
 *   <li>클라이언트에서 받은 idToken 검증</li>
 *   <li>idToken에서 사용자 정보 추출 (sub, email)</li>
 *   <li>가상 이메일 생성 (apple_{sub}@messageapp.com)</li>
 * </ol>
 *
 * <p>Note: Apple 로그인은 인가 코드 방식이 아닌 idToken 직접 검증 방식을 사용합니다.
 * 클라이언트(iOS)에서 Apple Sign in 완료 후 받은 idToken을 서버로 전달합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see OAuthLoginStrategy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppleLoginStrategy implements OAuthLoginStrategy {

    private final AppleIdTokenValidator appleIdTokenValidator;

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthProvider getProvider() {
        return OauthProvider.APPLE;
    }

    /**
     * Apple idToken을 검증하고 사용자 정보를 추출합니다.
     *
     * <p>다른 OAuth 전략과 달리 authorizationCode 파라미터에
     * Apple idToken이 전달됩니다.</p>
     *
     * @param idToken Apple에서 발급한 idToken (JWT)
     * @return Apple 사용자 정보
     */
    @Override
    public OAuthUserInfo authenticate(String idToken) {
        // idToken 검증 및 사용자 정보 추출
        AppleUserInfo userInfo = appleIdTokenValidator.validateAndExtract(idToken);

        // 가상 이메일 생성: Apple은 실제 이메일을 숨길 수 있으므로 sub 기반 가상 이메일 사용
        String virtualEmail = generateVirtualEmail(userInfo.sub());

        log.info("Apple 로그인 성공: sub = {}", userInfo.sub());

        return OAuthUserInfo.builder()
                .oauthId(userInfo.sub())
                .email(virtualEmail)
                .provider(OauthProvider.APPLE)
                .build();
    }

    /**
     * Apple 가상 이메일을 생성합니다.
     *
     * <p>형식: apple_{sub}@virtual.com</p>
     *
     * @param sub Apple 사용자 고유 ID
     * @return 생성된 가상 이메일
     */
    private String generateVirtualEmail(String sub) {
        return "apple_" + sub + "@virtual.com";
    }
}
