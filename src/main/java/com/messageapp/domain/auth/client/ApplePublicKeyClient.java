package com.messageapp.domain.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Apple 공개키 조회 Feign 클라이언트
 *
 * <p>Apple의 JWKS 엔드포인트에서 공개키를 조회합니다.
 * idToken 서명 검증에 사용됩니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@FeignClient(name = "applePublicKeyClient", url = "https://appleid.apple.com")
public interface ApplePublicKeyClient {

    /**
     * Apple 공개키(JWKS) 목록을 조회합니다.
     *
     * @return JWKS 형식의 공개키 응답
     */
    @GetMapping("/auth/keys")
    ApplePublicKeyResponse getPublicKeys();
}
