package com.messageapp.api.auth;

import com.messageapp.domain.auth.dto.JwtTokenResponse;
import com.messageapp.domain.auth.dto.KakaoLoginRequest;
import com.messageapp.domain.auth.dto.KakaoLoginUrlResponse;
import com.messageapp.domain.auth.dto.SignupCompleteRequest;
import com.messageapp.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    /**
     * 카카오 로그인 URL 받기
     */
    @GetMapping("/kakao/login-url")
    public KakaoLoginUrlResponse getKakaoLoginUrl() {
        String loginUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectUri +
                "&response_type=code" +
                "&prompt=login";
        return new KakaoLoginUrlResponse(loginUrl);
    }

    /**
     * 카카오 로그인 처리
     */
    @PostMapping("/login")
    public JwtTokenResponse kakaoLogin(@Valid @RequestBody KakaoLoginRequest kakaoLoginRequest) {
        return authService.kakaoLogin(kakaoLoginRequest.getCode());
    }

    /**
     * 회원가입 완료 (닉네임 입력)
     */
    @PostMapping("/signup/complete")
    public JwtTokenResponse completeSignup(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody SignupCompleteRequest request) {
        String token = authHeader.replace("Bearer ", "");
        return authService.completeSignup(token, request.getNickname());
    }
}
