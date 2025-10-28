package com.messageapp.domain.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 카카오 사용자 정보 조회를 위한 OpenFeign 클라이언트
 */
@FeignClient(
        name = "kakao-api",
        url = "https://kapi.kakao.com",
        configuration = KakaoFeignConfig.class
)
public interface KakaoApiClient {

    /**
     * 카카오 사용자 정보 조회
     */
    @GetMapping("/v2/user/me")
    KakaoUserResponse getUserInfo(@RequestHeader("Authorization") String accessToken);
}
