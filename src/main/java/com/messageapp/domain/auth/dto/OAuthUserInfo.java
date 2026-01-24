package com.messageapp.domain.auth.dto;

import com.messageapp.api.auth.OauthProvider;
import lombok.Builder;
import lombok.Getter;

/**
 * OAuth 인증 사용자 정보 DTO
 *
 * <p>다양한 OAuth 제공자(카카오, 구글 등)의 사용자 정보를
 * 통일된 형식으로 표현합니다.</p>
 *
 * <p>Strategy 패턴의 {@link com.messageapp.domain.auth.strategy.OAuthLoginStrategy}
 * 구현체들이 반환하는 공통 DTO입니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see com.messageapp.domain.auth.strategy.OAuthLoginStrategy
 */
@Getter
@Builder
public class OAuthUserInfo {

    /** OAuth 제공자에서 부여한 고유 사용자 ID */
    private final String oauthId;

    /** OAuth 제공자에서 생성한 가상 이메일 */
    private final String email;

    /** OAuth 제공자 (KAKAO, GOOGLE 등) */
    private final OauthProvider provider;

    /**
     * 가상 이메일을 생성합니다.
     *
     * <p>형식: {provider}_{oauthId}@virtual.com</p>
     * <p>예시: kakao_12345@virtual.com</p>
     *
     * @return 생성된 가상 이메일
     */
    public String generateVirtualEmail() {
        return provider.getValue().toLowerCase() + "_" + oauthId + "@virtual.com";
    }
}
