package com.messageapp.api.auth;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OAuth 제공자 열거형
 *
 * <p>지원하는 소셜 로그인 제공자를 정의합니다.</p>
 *
 * <h3>지원 제공자:</h3>
 * <ul>
 *   <li>KAKAO - 카카오 로그인</li>
 *   <li>GOOGLE - 구글 로그인</li>
 *   <li>APPLE - 애플 로그인</li>
 * </ul>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@AllArgsConstructor
@Getter
public enum OauthProvider {

    /** 카카오 소셜 로그인 */
    KAKAO("KAKAO"),

    /** 구글 소셜 로그인 */
    GOOGLE("GOOGLE"),

    /** 애플 소셜 로그인 */
    APPLE("APPLE");

    /** 제공자 문자열 값 */
    private String oauthProvider;

    /**
     * JSON 직렬화 시 사용되는 값을 반환합니다.
     *
     * @return 제공자 문자열 값
     */
    @JsonValue
    public String getValue() {
        return oauthProvider;
    }

    /**
     * 문자열에서 OauthProvider를 변환합니다.
     *
     * <p>대소문자를 구분하지 않습니다.</p>
     *
     * @param value 소셜 로그인 제공자 문자열 (예: "KAKAO", "kakao", "GOOGLE")
     * @return 해당하는 OauthProvider enum 값
     * @throws IllegalArgumentException 지원하지 않는 제공자인 경우
     */
    public static OauthProvider fromString(String value) {
        for (OauthProvider provider : values()) {
            if (provider.oauthProvider.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown OauthProvider: " + value);
    }
}
