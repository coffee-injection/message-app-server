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

/**
 * JWT 토큰 생성 및 검증 제공자
 *
 * <p>JWT(JSON Web Token) 생성, 파싱, 검증 기능을 제공합니다.</p>
 *
 * <h3>토큰 종류:</h3>
 * <ul>
 *   <li><b>access</b>: 회원가입이 완료된 정규 회원용 토큰</li>
 *   <li><b>temp</b>: 소셜 로그인 후 회원가입 전 임시 토큰</li>
 * </ul>
 *
 * <h3>토큰 구조:</h3>
 * <ul>
 *   <li>subject: 회원 ID (access) 또는 temp_{oauthId} (temp)</li>
 *   <li>email: 회원 이메일</li>
 *   <li>type: 토큰 종류 (access, temp)</li>
 *   <li>oauthId, oauthProvider: OAuth 정보 (temp 토큰에만 포함)</li>
 * </ul>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see JwtProperties
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    /** JWT 설정 프로퍼티 */
    private final JwtProperties jwtProperties;

    /**
     * Access Token을 생성합니다 (정규 회원용).
     *
     * @param memberId 회원 ID
     * @param email 회원 이메일
     * @return 생성된 JWT Access Token
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
     * 임시 Access Token을 생성합니다 (회원가입 전 신규 사용자용).
     *
     * <p>소셜 로그인 직후, 닉네임 등 추가 정보 입력 전에 발급됩니다.
     * 이 토큰으로 회원가입 완료 API를 호출할 수 있습니다.</p>
     *
     * @param oauthId OAuth 제공자의 고유 사용자 ID
     * @param email 가상 이메일
     * @param provider OAuth 제공자 (KAKAO, GOOGLE)
     * @return 생성된 임시 JWT Token
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
     * JWT 토큰에서 회원 ID를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 회원 ID
     */
    public Long getMemberIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * JWT 토큰에서 이메일을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 이메일
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * JWT 토큰에서 카카오 ID를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 카카오 ID
     * @deprecated {@link #getOauthIdFromToken(String)} 사용 권장
     */
    @Deprecated
    public String getKakaoIdFromToken(String token) {
        Claims claims = parseClaims(token);
        String oauthId = claims.get("oauthId", String.class);
        return oauthId != null ? oauthId : claims.get("kakaoId", String.class);
    }

    /**
     * JWT 토큰에서 OAuth ID를 추출합니다.
     *
     * <p>하위 호환성을 위해 oauthId 클레임이 없으면 kakaoId 클레임을 확인합니다.</p>
     *
     * @param token JWT 토큰
     * @return OAuth ID
     */
    public String getOauthIdFromToken(String token) {
        Claims claims = parseClaims(token);
        String oauthId = claims.get("oauthId", String.class);
        return oauthId != null ? oauthId : claims.get("kakaoId", String.class);
    }

    /**
     * JWT 토큰에서 OAuth 제공자를 추출합니다.
     *
     * <p>하위 호환성을 위해 oauthProvider 클레임이 없으면 KAKAO로 간주합니다.</p>
     *
     * @param token JWT 토큰
     * @return OAuth 제공자
     */
    public OauthProvider getOauthProviderFromToken(String token) {
        Claims claims = parseClaims(token);
        String providerValue = claims.get("oauthProvider", String.class);
        if (providerValue == null) {
            return OauthProvider.KAKAO;
        }
        return OauthProvider.fromString(providerValue);
    }

    /**
     * JWT 토큰의 타입을 확인합니다.
     *
     * @param token JWT 토큰
     * @return 토큰 타입 ("access" 또는 "temp")
     */
    public String getTokenType(String token) {
        Claims claims = parseClaims(token);
        return claims.get("type", String.class);
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     *
     * @param token JWT 토큰
     * @return 유효하면 true, 아니면 false
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
     * Refresh Token을 생성합니다.
     *
     * @param memberId 회원 ID
     * @return 생성된 Refresh Token
     */
    public String generateRefreshToken(Long memberId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Refresh Token의 유효성을 검증합니다.
     *
     * @param token Refresh Token
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parseClaims(token);
            String tokenType = claims.get("type", String.class);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            log.error("Refresh Token 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Refresh Token에서 회원 ID를 추출합니다.
     *
     * @param token Refresh Token
     * @return 회원 ID
     */
    public Long getMemberIdFromRefreshToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Refresh Token의 만료 시간을 반환합니다 (밀리초).
     *
     * @return 만료 시간 (밀리초)
     */
    public Long getRefreshTokenExpiration() {
        return jwtProperties.getRefreshTokenExpiration();
    }

    /**
     * JWT 토큰을 파싱하여 Claims를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 파싱된 Claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * HMAC-SHA 서명에 사용할 SecretKey를 생성합니다.
     *
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
