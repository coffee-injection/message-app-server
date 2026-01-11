package com.messageapp.domain.auth.client;

import com.messageapp.domain.auth.dto.GoogleTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(
        name = "google-oauth",
        url = "https://oauth2.googleapis.com",
        configuration = KakaoFeignConfig.class
)
public interface GoogleOAuthClient {

    /**
     * 인가 코드로 구글 Access Token 발급
     */
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    GoogleTokenResponse getAccessToken(Map<String, ?> params);
}
