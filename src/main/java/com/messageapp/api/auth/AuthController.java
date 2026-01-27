package com.messageapp.api.auth;

import com.messageapp.domain.auth.dto.GoogleLoginRequest;
import com.messageapp.domain.auth.dto.GoogleLoginUrlResponse;
import com.messageapp.domain.auth.dto.JwtTokenResponse;
import com.messageapp.domain.auth.dto.KakaoLoginRequest;
import com.messageapp.domain.auth.dto.KakaoLoginUrlResponse;
import com.messageapp.domain.auth.dto.SignupCompleteRequest;
import com.messageapp.domain.auth.dto.TokenRefreshRequest;
import com.messageapp.domain.auth.service.AuthService;
import com.messageapp.global.auth.LoginMember;
import com.messageapp.global.config.OAuthProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 REST API 컨트롤러
 *
 * <p>OAuth 소셜 로그인, 회원가입 완료, 회원 탈퇴 API를 제공합니다.</p>
 *
 * <h3>API 목록:</h3>
 * <ul>
 *   <li>GET /api/v1/auth/kakao/login-url - 카카오 로그인 URL 조회</li>
 *   <li>POST /api/v1/auth/login - 카카오 로그인</li>
 *   <li>GET /api/v1/auth/google/login-url - 구글 로그인 URL 조회</li>
 *   <li>POST /api/v1/auth/google/login - 구글 로그인</li>
 *   <li>POST /api/v1/auth/signup/complete - 회원가입 완료</li>
 *   <li>DELETE /api/v1/auth/withdraw - 회원 탈퇴</li>
 * </ul>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see AuthService
 */
@Tag(name = "인증", description = "인증 관련 API")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    /** 인증 서비스 */
    private final AuthService authService;

    /** OAuth 설정 프로퍼티 */
    private final OAuthProperties oAuthProperties;

    @Operation(summary = "카카오 로그인 URL 조회", description = "카카오 OAuth 로그인 URL을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = KakaoLoginUrlResponse.class)))
    })
    @GetMapping("/kakao/login-url")
    public KakaoLoginUrlResponse getKakaoLoginUrl() {
        String loginUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + oAuthProperties.getKakao().getClientId() +
                "&redirect_uri=" + oAuthProperties.getKakao().getRedirectUri() +
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

    @Operation(summary = "구글 로그인 URL 조회", description = "구글 OAuth 로그인 URL을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = GoogleLoginUrlResponse.class)))
    })
    @GetMapping("/google/login-url")
    public GoogleLoginUrlResponse getGoogleLoginUrl() {
        String loginUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + oAuthProperties.getGoogle().getClientId() +
                "&redirect_uri=" + oAuthProperties.getGoogle().getRedirectUri() +
                "&response_type=code" +
                "&scope=openid email profile" +
                "&access_type=offline" +
                "&prompt=consent";
        return new GoogleLoginUrlResponse(loginUrl);
    }

    @Operation(summary = "구글 로그인", description = "구글 인가 코드로 로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = JwtTokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/google/login")
    public JwtTokenResponse googleLogin(@Valid @RequestBody GoogleLoginRequest googleLoginRequest) {
        return authService.googleLogin(googleLoginRequest.getCode());
    }

    @Operation(summary = "회원가입 완료", description = "닉네임, 섬 이름, 프로필 이미지를 입력하여 회원가입을 완료하고 JWT 토큰을 발급받습니다.")
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
        return authService.completeSignup(token, request.getNickname(), request.getIslandName(), request.getProfileImageIndex());
    }

    @Operation(
            summary = "토큰 갱신",
            description = "Refresh Token으로 새로운 Access Token과 Refresh Token을 발급받습니다.\n\n" +
                    "**Refresh Token Rotation**: 보안 강화를 위해 매 갱신 시 새로운 Refresh Token이 발급됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공",
                    content = @Content(schema = @Schema(implementation = JwtTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token", content = @Content),
            @ApiResponse(responseCode = "403", description = "비활성화된 회원 또는 만료된 Refresh Token", content = @Content)
    })
    @PostMapping("/refresh")
    public JwtTokenResponse refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return authService.refreshToken(request.getRefreshToken());
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 회원을 탈퇴 처리합니다. 카카오 연결이 끊어지고 회원 상태가 INACTIVE로 변경됩니다.\n\n" +
                    "**인증 필요**: Authorization 헤더에 Bearer 토큰을 포함해야 합니다.\n\n" +
                    "**파라미터 없음**: JWT 토큰에서 자동으로 회원 정보를 추출합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (이미 탈퇴한 회원)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "카카오 연결 끊기 실패", content = @Content)
    })
    @DeleteMapping("/withdraw")
    public void withdrawMember(@Parameter(hidden = true) @LoginMember Long memberId) {
        authService.withdrawMember(memberId);
    }
}
