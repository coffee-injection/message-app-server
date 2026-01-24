package com.messageapp.domain.auth.strategy;

import com.messageapp.api.auth.OauthProvider;
import com.messageapp.domain.auth.client.GoogleApiClient;
import com.messageapp.domain.auth.client.GoogleOAuthClient;
import com.messageapp.domain.auth.client.GoogleUserResponse;
import com.messageapp.domain.auth.dto.GoogleTokenResponse;
import com.messageapp.domain.auth.dto.OAuthUserInfo;
import com.messageapp.global.config.OAuthProperties;
import com.messageapp.global.constant.OAuthConstants;
import com.messageapp.global.exception.external.GoogleLoginFailedException;
import com.messageapp.global.exception.external.GoogleUserInfoFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 구글 로그인 전략 구현체
 *
 * <p>구글 OAuth 2.0 인증 플로우를 처리합니다.</p>
 *
 * <h3>인증 플로우:</h3>
 * <ol>
 *   <li>인가 코드 URL 디코딩</li>
 *   <li>인가 코드로 구글 Access Token 발급</li>
 *   <li>Access Token으로 구글 사용자 정보 조회</li>
 *   <li>가상 이메일 생성 (google_{id}@messageapp.com)</li>
 * </ol>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see OAuthLoginStrategy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleLoginStrategy implements OAuthLoginStrategy {

    /** 구글 OAuth 토큰 발급 클라이언트 */
    private final GoogleOAuthClient googleOAuthClient;

    /** 구글 API 클라이언트 (사용자 정보 조회) */
    private final GoogleApiClient googleApiClient;

    /** OAuth 설정 프로퍼티 */
    private final OAuthProperties oAuthProperties;

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthProvider getProvider() {
        return OauthProvider.GOOGLE;
    }

    /**
     * 구글 OAuth 인증을 수행합니다.
     *
     * @param authorizationCode 구글 인가 코드 (URL 인코딩된 상태)
     * @return 구글 사용자 정보
     * @throws GoogleLoginFailedException Access Token 발급 실패 시
     * @throws GoogleUserInfoFailedException 사용자 정보 조회 실패 시
     */
    @Override
    public OAuthUserInfo authenticate(String authorizationCode) {
        // 1. 인가 코드 URL 디코딩 (구글은 URL 인코딩된 코드를 전달함)
        String decodedCode = URLDecoder.decode(authorizationCode, StandardCharsets.UTF_8);

        // 2. 인가 코드로 Access Token 발급
        GoogleTokenResponse tokenResponse = getAccessToken(decodedCode);

        // 3. Access Token으로 사용자 정보 조회
        GoogleUserResponse userResponse = getUserInfo(tokenResponse.getAccessToken());

        // 4. OAuthUserInfo 객체 생성 및 반환
        return OAuthUserInfo.builder()
                .oauthId(userResponse.getId())
                .email(userResponse.generateVirtualEmail())
                .provider(OauthProvider.GOOGLE)
                .build();
    }

    /**
     * 구글 Access Token을 발급받습니다.
     *
     * @param authorizationCode 인가 코드 (디코딩된 상태)
     * @return 토큰 응답 객체
     * @throws GoogleLoginFailedException 토큰 발급 실패 시
     */
    private GoogleTokenResponse getAccessToken(String authorizationCode) {
        try {
            // 구글 토큰 요청 파라미터 구성
            Map<String, String> params = new HashMap<>();
            params.put("grant_type", OAuthConstants.GRANT_TYPE_AUTHORIZATION_CODE);
            params.put("client_id", oAuthProperties.getGoogle().getClientId());
            params.put("client_secret", oAuthProperties.getGoogle().getClientSecret());
            params.put("redirect_uri", oAuthProperties.getGoogle().getRedirectUri());
            params.put("code", authorizationCode);

            return googleOAuthClient.getAccessToken(params);
        } catch (Exception e) {
            log.error("구글 Access Token 발급 실패: {}", e.getMessage());
            throw new GoogleLoginFailedException();
        }
    }

    /**
     * 구글 사용자 정보를 조회합니다.
     *
     * @param accessToken 구글 Access Token
     * @return 구글 사용자 정보
     * @throws GoogleUserInfoFailedException 조회 실패 시
     */
    private GoogleUserResponse getUserInfo(String accessToken) {
        try {
            return googleApiClient.getUserInfo(OAuthConstants.TOKEN_TYPE_BEARER + " " + accessToken);
        } catch (Exception e) {
            log.error("구글 사용자 정보 조회 실패: {}", e.getMessage());
            throw new GoogleUserInfoFailedException();
        }
    }
}
