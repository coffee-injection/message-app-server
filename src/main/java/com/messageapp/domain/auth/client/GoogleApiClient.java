package com.messageapp.domain.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 구글 사용자 정보 조회를 위한 OpenFeign 클라이언트
 */
@FeignClient(
        name = "google-api",
        url = "https://www.googleapis.com",
        configuration = KakaoFeignConfig.class
)
public interface GoogleApiClient {

    /**
     * 구글 사용자 정보 조회
     */
    @GetMapping("/oauth2/v2/userinfo")
    GoogleUserResponse getUserInfo(@RequestHeader("Authorization") String accessToken);
}
