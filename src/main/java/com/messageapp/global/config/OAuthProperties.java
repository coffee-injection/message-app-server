package com.messageapp.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OAuth 인증 설정 프로퍼티 클래스
 *
 * <p>application.yml의 oauth 설정을 Type-safe하게 바인딩합니다.
 * {@code @Value} 어노테이션 대신 사용하여 설정 관리를 중앙화합니다.</p>
 *
 * <p>설정 예시:</p>
 * <pre>
 * oauth:
 *   kakao:
 *     client-id: xxx
 *     client-secret: xxx
 *   google:
 *     client-id: xxx
 * </pre>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {

    /** 카카오 OAuth 설정 */
    private KakaoProperties kakao = new KakaoProperties();

    /** 구글 OAuth 설정 */
    private GoogleProperties google = new GoogleProperties();

    /**
     * 카카오 OAuth 설정 프로퍼티
     */
    @Getter
    @Setter
    public static class KakaoProperties {
        /** 카카오 앱 Client ID (REST API 키) */
        private String clientId;

        /** 카카오 앱 Client Secret */
        private String clientSecret;

        /** 카카오 로그인 후 리다이렉트 URI */
        private String redirectUri;

        /** 카카오 Admin Key (사용자 연결 해제 시 사용) */
        private String adminKey;
    }

    /**
     * 구글 OAuth 설정 프로퍼티
     */
    @Getter
    @Setter
    public static class GoogleProperties {
        /** 구글 앱 Client ID */
        private String clientId;

        /** 구글 앱 Client Secret */
        private String clientSecret;

        /** 구글 로그인 후 리다이렉트 URI */
        private String redirectUri;
    }
}
