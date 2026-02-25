package com.messageapp.domain.auth.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messageapp.domain.auth.client.ApplePublicKeyClient;
import com.messageapp.domain.auth.client.ApplePublicKeyResponse;
import com.messageapp.global.config.OAuthProperties;
import com.messageapp.global.exception.external.AppleLoginFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

/**
 * Apple idToken 검증기
 *
 * <p>Apple에서 발급한 idToken(JWT)의 서명을 검증하고
 * 클레임(sub, email 등)을 추출합니다.</p>
 *
 * <h3>검증 항목:</h3>
 * <ul>
 *   <li>JWT 서명 검증 (Apple 공개키 사용)</li>
 *   <li>issuer(iss) 확인: https://appleid.apple.com</li>
 *   <li>audience(aud) 확인: 앱 Bundle ID</li>
 *   <li>만료시간(exp) 확인</li>
 * </ul>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppleIdTokenValidator {

    private static final String APPLE_ISSUER = "https://appleid.apple.com";
    private static final String RSA_ALGORITHM = "SHA256withRSA";

    private final ApplePublicKeyClient applePublicKeyClient;
    private final OAuthProperties oAuthProperties;
    private final ObjectMapper objectMapper;

    /**
     * Apple idToken을 검증하고 사용자 정보를 추출합니다.
     *
     * @param idToken Apple에서 발급한 JWT 토큰
     * @return 사용자 정보 (sub, email)
     * @throws AppleLoginFailedException 토큰 검증 실패 시
     */
    public AppleUserInfo validateAndExtract(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length != 3) {
                throw new AppleLoginFailedException("Invalid idToken format");
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            byte[] signatureBytes = Base64.getUrlDecoder().decode(parts[2]);

            // 헤더에서 kid 추출
            @SuppressWarnings("unchecked")
            Map<String, Object> header = objectMapper.readValue(headerJson, Map.class);
            String kid = (String) header.get("kid");

            // Apple 공개키 조회 및 서명 검증
            ApplePublicKeyResponse publicKeyResponse = applePublicKeyClient.getPublicKeys();
            ApplePublicKeyResponse.Key matchedKey = publicKeyResponse.getMatchedKey(kid);

            if (matchedKey == null) {
                throw new AppleLoginFailedException("No matching public key found for kid: " + kid);
            }

            // RSA 공개키 생성 및 서명 검증
            PublicKey publicKey = generatePublicKey(matchedKey);
            if (!verifySignature(parts[0] + "." + parts[1], signatureBytes, publicKey)) {
                throw new AppleLoginFailedException("Invalid idToken signature");
            }

            // 페이로드에서 클레임 추출 및 검증
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = objectMapper.readValue(payloadJson, Map.class);

            validateClaims(payload);

            String sub = (String) payload.get("sub");
            String email = (String) payload.get("email");

            log.info("Apple idToken 검증 성공: sub = {}", sub);

            return new AppleUserInfo(sub, email);

        } catch (AppleLoginFailedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Apple idToken 검증 실패: {}", e.getMessage());
            throw new AppleLoginFailedException("Failed to validate idToken: " + e.getMessage());
        }
    }

    /**
     * RSA 공개키를 생성합니다.
     */
    private PublicKey generatePublicKey(ApplePublicKeyResponse.Key key) throws Exception {
        byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
        byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(n, e);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(spec);
    }

    /**
     * JWT 서명을 검증합니다.
     */
    private boolean verifySignature(String signedData, byte[] signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance(RSA_ALGORITHM);
        sig.initVerify(publicKey);
        sig.update(signedData.getBytes());
        return sig.verify(signature);
    }

    /**
     * JWT 클레임을 검증합니다.
     */
    private void validateClaims(Map<String, Object> payload) {
        // issuer 검증
        String iss = (String) payload.get("iss");
        if (!APPLE_ISSUER.equals(iss)) {
            throw new AppleLoginFailedException("Invalid issuer: " + iss);
        }

        // audience 검증
        String aud = (String) payload.get("aud");
        String expectedAud = oAuthProperties.getApple().getClientId();
        if (!expectedAud.equals(aud)) {
            throw new AppleLoginFailedException("Invalid audience: " + aud);
        }

        // 만료시간 검증
        Object expObj = payload.get("exp");
        long exp;
        if (expObj instanceof Integer) {
            exp = ((Integer) expObj).longValue();
        } else if (expObj instanceof Long) {
            exp = (Long) expObj;
        } else {
            throw new AppleLoginFailedException("Invalid exp claim type");
        }

        long now = System.currentTimeMillis() / 1000;
        if (exp < now) {
            throw new AppleLoginFailedException("Token has expired");
        }
    }

    /**
     * Apple 사용자 정보를 담는 record
     */
    public record AppleUserInfo(String sub, String email) {
    }
}
