package com.messageapp.global.jwt;

import com.messageapp.api.auth.OauthProvider;
import com.messageapp.global.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * Access Token 생성 (회원가입 완료된 사용자용)
     */
    public String generateAccessToken(Long memberId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("email", email)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 임시 Access Token 생성 (소셜 로그인 직후, 닉네임 입력 전)
     */
    public String generateTempAccessToken(String oauthId, String email, OauthProvider provider) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject("temp_" + oauthId)
                .claim("oauthId", oauthId)
                .claim("oauthProvider", provider.getValue())
                .claim("email", email)
                .claim("type", "temp")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     */
    public Long getMemberIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * JWT 토큰에서 이메일 추출
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * JWT 토큰에서 kakaoId 추출 (하위 호환성 유지)
     * @deprecated Use {@link #getOauthIdFromToken(String)} instead
     */
    @Deprecated
    public String getKakaoIdFromToken(String token) {
        Claims claims = parseClaims(token);
        // 먼저 oauthId 클레임 확인, 없으면 기존 kakaoId 클레임 확인 (하위 호환성)
        String oauthId = claims.get("oauthId", String.class);
        return oauthId != null ? oauthId : claims.get("kakaoId", String.class);
    }

    /**
     * JWT 토큰에서 oauthId 추출
     */
    public String getOauthIdFromToken(String token) {
        Claims claims = parseClaims(token);
        // 먼저 oauthId 클레임 확인, 없으면 기존 kakaoId 클레임 확인 (하위 호환성)
        String oauthId = claims.get("oauthId", String.class);
        return oauthId != null ? oauthId : claims.get("kakaoId", String.class);
    }

    /**
     * JWT 토큰에서 oauthProvider 추출
     */
    public OauthProvider getOauthProviderFromToken(String token) {
        Claims claims = parseClaims(token);
        String providerValue = claims.get("oauthProvider", String.class);
        // 하위 호환성: oauthProvider 클레임이 없으면 KAKAO로 간주
        if (providerValue == null) {
            return OauthProvider.KAKAO;
        }
        return OauthProvider.fromString(providerValue);
    }

    /**
     * JWT 토큰 타입 확인 (temp인지 access인지)
     */
    public String getTokenType(String token) {
        Claims claims = parseClaims(token);
        return claims.get("type", String.class);
    }

    /**
     * JWT 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT 토큰 파싱
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 서명 키 생성
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
