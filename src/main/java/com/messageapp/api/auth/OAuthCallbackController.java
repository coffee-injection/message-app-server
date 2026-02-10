package com.messageapp.api.auth;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * OAuth 콜백 컨트롤러
 *
 * <p>OAuth 인증 후 앱 딥링크로 리다이렉트합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Slf4j
@Hidden
@RestController
public class OAuthCallbackController {

    private static final String GOOGLE_APP_DEEP_LINK = "coffeeinjection://auth/google/callback";

    /**
     * 구글 OAuth 콜백을 처리하고 앱 딥링크로 리다이렉트합니다.
     *
     * @param code 구글 인가 코드
     * @return 앱 딥링크로 302 리다이렉트
     */
    @GetMapping("/auth/google/callback")
    public ResponseEntity<Void> googleCallback(@RequestParam String code) {
        log.info("구글 OAuth 콜백 수신, 앱으로 리다이렉트");

        String encodedCode = URLEncoder.encode(code, StandardCharsets.UTF_8);
        String deepLink = GOOGLE_APP_DEEP_LINK + "?code=" + encodedCode;

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(deepLink))
                .build();
    }
}
