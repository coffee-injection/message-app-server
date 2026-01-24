package com.messageapp.domain.auth.strategy;

import com.messageapp.api.auth.OauthProvider;
import com.messageapp.domain.auth.dto.OAuthUserInfo;

/**
 * OAuth 로그인 전략 인터페이스
 *
 * <p>Strategy 패턴을 적용하여 다양한 OAuth 제공자(카카오, 구글 등)의
 * 로그인 로직을 통일된 인터페이스로 추상화합니다.</p>
 *
 * <p>새로운 OAuth 제공자를 추가하려면 이 인터페이스를 구현하면 됩니다.</p>
 *
 * <h3>구현 예시:</h3>
 * <pre>
 * {@code
 * @Component
 * public class NaverLoginStrategy implements OAuthLoginStrategy {
 *     @Override
 *     public OauthProvider getProvider() {
 *         return OauthProvider.NAVER;
 *     }
 *
 *     @Override
 *     public OAuthUserInfo authenticate(String authorizationCode) {
 *         // 네이버 로그인 로직 구현
 *     }
 * }
 * }
 * </pre>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see KakaoLoginStrategy
 * @see GoogleLoginStrategy
 */
public interface OAuthLoginStrategy {

    /**
     * 해당 전략이 지원하는 OAuth 제공자를 반환합니다.
     *
     * @return OAuth 제공자 (KAKAO, GOOGLE 등)
     */
    OauthProvider getProvider();

    /**
     * OAuth 인가 코드를 사용하여 사용자 인증을 수행합니다.
     *
     * <p>인가 코드로 Access Token을 발급받고,
     * 해당 토큰으로 사용자 정보를 조회합니다.</p>
     *
     * @param authorizationCode OAuth 인가 코드
     * @return 인증된 사용자 정보
     * @throws com.messageapp.global.error.AppException 인증 실패 시
     */
    OAuthUserInfo authenticate(String authorizationCode);
}
