package com.messageapp.domain.auth.strategy;

import com.messageapp.api.auth.OauthProvider;
import com.messageapp.domain.auth.client.KakaoApiClient;
import com.messageapp.domain.auth.client.KakaoOAuthClient;
import com.messageapp.domain.auth.client.KakaoUserResponse;
import com.messageapp.domain.auth.dto.KakaoTokenResponse;
import com.messageapp.domain.auth.dto.OAuthUserInfo;
import com.messageapp.global.config.OAuthProperties;
import com.messageapp.global.constant.OAuthConstants;
import com.messageapp.global.exception.external.KakaoLoginFailedException;
import com.messageapp.global.exception.external.KakaoUserInfoFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 카카오 로그인 전략 구현체
 *
 * <p>카카오 OAuth 2.0 인증 플로우를 처리합니다.</p>
 *
 * <h3>인증 플로우:</h3>
 * <ol>
 *   <li>인가 코드로 카카오 Access Token 발급</li>
 *   <li>Access Token으로 카카오 사용자 정보 조회</li>
 *   <li>가상 이메일 생성 (kakao_{id}@kakao.com)</li>
 * </ol>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see OAuthLoginStrategy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoLoginStrategy implements OAuthLoginStrategy {

    /** 카카오 OAuth 토큰 발급 클라이언트 */
    private final KakaoOAuthClient kakaoOAuthClient;

    /** 카카오 API 클라이언트 (사용자 정보 조회) */
    private final KakaoApiClient kakaoApiClient;

    /** OAuth 설정 프로퍼티 */
    private final OAuthProperties oAuthProperties;

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthProvider getProvider() {
        return OauthProvider.KAKAO;
    }

    /**
     * 카카오 OAuth 인증을 수행합니다.
     *
     * @param authorizationCode 카카오 인가 코드
     * @return 카카오 사용자 정보
     * @throws KakaoLoginFailedException Access Token 발급 실패 시
     * @throws KakaoUserInfoFailedException 사용자 정보 조회 실패 시
     */
    @Override
    public OAuthUserInfo authenticate(String authorizationCode) {
        // 1. 인가 코드로 Access Token 발급
        KakaoTokenResponse tokenResponse = getAccessToken(authorizationCode);

        // 2. Access Token으로 사용자 정보 조회
        KakaoUserResponse userResponse = getUserInfo(tokenResponse.getAccessToken());

        // 3. OAuthUserInfo 객체 생성 및 반환
        return OAuthUserInfo.builder()
                .oauthId(String.valueOf(userResponse.getId()))
                .email(userResponse.generateVirtualEmail())
                .provider(OauthProvider.KAKAO)
                .build();
    }

    /**
     * 카카오 Access Token을 발급받습니다.
     *
     * @param authorizationCode 인가 코드
     * @return 토큰 응답 객체
     * @throws KakaoLoginFailedException 토큰 발급 실패 시
     */
    private KakaoTokenResponse getAccessToken(String authorizationCode) {
        try {
            return kakaoOAuthClient.getAccessToken(
                    OAuthConstants.GRANT_TYPE_AUTHORIZATION_CODE,
                    oAuthProperties.getKakao().getClientId(),
                    oAuthProperties.getKakao().getClientSecret(),
                    oAuthProperties.getKakao().getRedirectUri(),
                    authorizationCode
            );
        } catch (Exception e) {
            log.error("카카오 Access Token 발급 실패: {}", e.getMessage());
            throw new KakaoLoginFailedException();
        }
    }

    /**
     * 카카오 사용자 정보를 조회합니다.
     *
     * @param accessToken 카카오 Access Token
     * @return 카카오 사용자 정보
     * @throws KakaoUserInfoFailedException 조회 실패 시
     */
    private KakaoUserResponse getUserInfo(String accessToken) {
        try {
            return kakaoApiClient.getUserInfo(OAuthConstants.TOKEN_TYPE_BEARER + " " + accessToken);
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage());
            throw new KakaoUserInfoFailedException();
        }
    }
}
