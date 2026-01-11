package com.messageapp.domain.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

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

    /**
     * 카카오 연결 끊기 (회원 탈퇴)
     */
    @PostMapping(value = "/v1/user/unlink", consumes = "application/x-www-form-urlencoded")
    void unlinkUser(
            @RequestHeader("Authorization") String adminKey,
            @RequestParam("target_id_type") String targetIdType,
            @RequestParam("target_id") String targetId
    );
}
