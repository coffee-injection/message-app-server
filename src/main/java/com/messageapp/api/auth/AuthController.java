package com.messageapp.api.auth;

import com.messageapp.domain.auth.dto.JwtTokenResponse;
import com.messageapp.domain.auth.dto.KakaoLoginRequest;
import com.messageapp.domain.auth.dto.KakaoLoginUrlResponse;
import com.messageapp.domain.auth.dto.SignupCompleteRequest;
import com.messageapp.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "인증 관련 API")
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

    @Operation(summary = "카카오 로그인 URL 조회", description = "카카오 OAuth 로그인 URL을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = KakaoLoginUrlResponse.class)))
    })
    @GetMapping("/kakao/login-url")
    public KakaoLoginUrlResponse getKakaoLoginUrl() {
        String loginUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectUri +
                "&response_type=code" +
                "&prompt=login";
        return new KakaoLoginUrlResponse(loginUrl);
    }

    @Operation(summary = "카카오 로그인", description = "카카오 인가 코드로 로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = JwtTokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/login")
    public JwtTokenResponse kakaoLogin(@Valid @RequestBody KakaoLoginRequest kakaoLoginRequest) {
        return authService.kakaoLogin(kakaoLoginRequest.getCode());
    }

    @Operation(summary = "회원가입 완료", description = "닉네임을 입력하여 회원가입을 완료하고 JWT 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 완료",
                    content = @Content(schema = @Schema(implementation = JwtTokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/signup/complete")
    public JwtTokenResponse completeSignup(
            @Parameter(description = "임시 JWT 토큰", required = true)
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody SignupCompleteRequest request) {
        String token = authHeader.replace("Bearer ", "");
        return authService.completeSignup(token, request.getNickname());
    }
}
