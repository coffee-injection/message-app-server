package com.messageapp.domain.auth.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Apple 공개키 응답 DTO
 *
 * <p>Apple의 JWKS(JSON Web Key Set) 응답을 매핑합니다.
 * https://appleid.apple.com/auth/keys 에서 반환되는 형식입니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Getter
@NoArgsConstructor
public class ApplePublicKeyResponse {

    private List<Key> keys;

    /**
     * JWKS의 개별 키 정보
     */
    @Getter
    @NoArgsConstructor
    public static class Key {
        /** Key Type (RSA) */
        private String kty;

        /** Key ID */
        private String kid;

        /** Algorithm (RS256) */
        private String alg;

        /** Use (sig - signature) */
        private String use;

        /** RSA 모듈러스 (Base64 URL 인코딩) */
        private String n;

        /** RSA 지수 (Base64 URL 인코딩) */
        private String e;
    }

    /**
     * kid로 해당하는 키를 찾습니다.
     *
     * @param kid Key ID
     * @return 일치하는 키, 없으면 null
     */
    public Key getMatchedKey(String kid) {
        return keys.stream()
                .filter(key -> key.getKid().equals(kid))
                .findFirst()
                .orElse(null);
    }
}
