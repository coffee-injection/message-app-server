package com.messageapp.api.auth;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OauthProvider {
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE");
    private String oauthProvider;

    @JsonValue
    public String getValue() {
        return oauthProvider;
    }

    /**
     * String 값에서 OauthProvider Enum으로 변환
     * @param value 소셜 로그인 제공자 문자열 (예: "KAKAO", "GOOGLE")
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
