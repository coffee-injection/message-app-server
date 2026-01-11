package com.messageapp.domain.auth.service;

import com.messageapp.domain.auth.client.GoogleApiClient;
import com.messageapp.domain.auth.client.GoogleOAuthClient;
import com.messageapp.domain.auth.client.GoogleUserResponse;
import com.messageapp.domain.auth.client.KakaoApiClient;
import com.messageapp.domain.auth.client.KakaoOAuthClient;
import com.messageapp.domain.auth.client.KakaoUserResponse;
import com.messageapp.domain.auth.dto.GoogleTokenResponse;
import com.messageapp.domain.auth.dto.JwtTokenResponse;
import com.messageapp.domain.auth.dto.KakaoTokenResponse;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import com.messageapp.global.config.JwtProperties;
import com.messageapp.global.exception.auth.InvalidAccessTokenException;
import com.messageapp.global.exception.business.member.DuplicateMemberException;
import com.messageapp.global.exception.business.member.MemberAlreadyWithdrawnException;
import com.messageapp.global.exception.business.member.MemberNotFoundException;
import com.messageapp.global.exception.external.GoogleLoginFailedException;
import com.messageapp.global.exception.external.GoogleUserInfoFailedException;
import com.messageapp.global.exception.external.KakaoLoginFailedException;
import com.messageapp.global.exception.external.KakaoUnlinkFailedException;
import com.messageapp.global.exception.external.KakaoUserInfoFailedException;
import com.messageapp.global.exception.validation.InvalidTempTokenException;
import com.messageapp.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final KakaoApiClient kakaoApiClient;
    private final GoogleOAuthClient googleOAuthClient;
    private final GoogleApiClient googleApiClient;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${oauth.kakao.admin-key}")
    private String kakaoAdminKey;

    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

    @Override
    @Transactional
    public JwtTokenResponse kakaoLogin(String authorizationCode) {

        // 1. 인가 코드로 카카오 Access Token 을 발급받는다.
        KakaoTokenResponse kakaoToken = getKakaoAccessToken(authorizationCode);

        // 2. 카카오 Access Token 으로 사용자 정보를 조회한다.
        KakaoUserResponse kakaoUser = getKakaoUserInfo(kakaoToken.getAccessToken());

        // 3. 가상 이메일을 생성한다.
        String virtualEmail = kakaoUser.generateVirtualEmail();
        String kakaoId = String.valueOf(kakaoUser.getId());

        // 4. 기존 회원인지 확인
        Member existingMember = memberRepository.findByEmail(virtualEmail).orElse(null);

        if (existingMember != null) {
            // 기존 회원 - 일반 JWT 발급
            String accessToken = jwtTokenProvider.generateAccessToken(existingMember.getId(), existingMember.getEmail());

            log.info("기존 회원 로그인 성공: memberId = {}, email = {}", existingMember.getId(), virtualEmail);

            return JwtTokenResponse.builder()
                    .accessToken(accessToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                    .memberId(existingMember.getId())
                    .email(existingMember.getEmail())
                    .isNewMember(false)
                    .nickname(existingMember.getName())
                    .islandName(existingMember.getIslandName())
                    .profileImageIndex(existingMember.getProfileImageIndex())
                    .build();
        } else {
            // 신규 회원 - 임시 JWT 발급 (DB 저장 X)
            String tempAccessToken = jwtTokenProvider.generateTempAccessToken(kakaoId, virtualEmail);

            log.info("신규 회원 카카오 인증 성공: kakaoId = {}, email = {}", kakaoId, virtualEmail);

            return JwtTokenResponse.builder()
                    .accessToken(tempAccessToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                    .memberId(null) // 아직 회원가입 전이므로 null
                    .email(virtualEmail)
                    .isNewMember(true)
                    .build();
        }
    }

    @Override
    @Transactional
    public JwtTokenResponse googleLogin(String authorizationCode) {

        authorizationCode = URLDecoder.decode(authorizationCode, StandardCharsets.UTF_8);

        // 1. 인가 코드로 구글 Access Token 을 발급받는다.
        GoogleTokenResponse googleToken = getGoogleAccessToken(authorizationCode);

        // 2. 구글 Access Token 으로 사용자 정보를 조회한다.
        GoogleUserResponse googleUser = getGoogleUserInfo(googleToken.getAccessToken());

        // 3. 가상 이메일을 생성한다.
        String virtualEmail = googleUser.generateVirtualEmail();
        String googleId = googleUser.getId();

        // 4. 기존 회원인지 확인
        Member existingMember = memberRepository.findByEmail(virtualEmail).orElse(null);

        if (existingMember != null) {
            // 기존 회원 - 일반 JWT 발급
            String accessToken = jwtTokenProvider.generateAccessToken(existingMember.getId(), existingMember.getEmail());

            log.info("기존 회원 로그인 성공: memberId = {}, email = {}", existingMember.getId(), virtualEmail);

            return JwtTokenResponse.builder()
                    .accessToken(accessToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                    .memberId(existingMember.getId())
                    .email(existingMember.getEmail())
                    .isNewMember(false)
                    .nickname(existingMember.getName())
                    .islandName(existingMember.getIslandName())
                    .profileImageIndex(existingMember.getProfileImageIndex())
                    .build();
        } else {
            // 신규 회원 - 임시 JWT 발급 (DB 저장 X)
            String tempAccessToken = jwtTokenProvider.generateTempAccessToken(googleId, virtualEmail);

            log.info("신규 회원 구글 인증 성공: googleId = {}, email = {}", googleId, virtualEmail);

            return JwtTokenResponse.builder()
                    .accessToken(tempAccessToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                    .memberId(null) // 아직 회원가입 전이므로 null
                    .email(virtualEmail)
                    .isNewMember(true)
                    .build();
        }
    }

    /**
     * 인가 코드로 카카오 Access Token 발급
     */
    private KakaoTokenResponse getKakaoAccessToken(String authorizationCode) {
        try {
            return kakaoOAuthClient.getAccessToken(
                    "authorization_code",
                    kakaoClientId,
                    kakaoClientSecret,
                    kakaoRedirectUri,
                    authorizationCode
            );
        } catch (Exception e) {
            log.error("카카오 Access Token 발급 실패: {}", e.getMessage());
            throw KakaoLoginFailedException.EXCEPTION;
        }
    }

    /**
     * 인가 코드로 구글 Access Token 발급
     */
    private GoogleTokenResponse getGoogleAccessToken(String authorizationCode) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("grant_type", "authorization_code");
            params.put("client_id", googleClientId);
            params.put("client_secret", googleClientSecret);
            params.put("redirect_uri", googleRedirectUri);
            params.put("code", authorizationCode);

            return googleOAuthClient.getAccessToken(params);
        } catch (Exception e) {
            log.error("구글 Access Token 발급 실패: {}", e.getMessage());
            throw GoogleLoginFailedException.EXCEPTION;
        }
    }

    @Override
    @Transactional
    public JwtTokenResponse completeSignup(String token, String nickname, String islandName, Integer profileImageIndex) {
        // 1. JWT 검증 및 타입 확인
        if (!jwtTokenProvider.validateToken(token)) {
            throw InvalidAccessTokenException.EXCEPTION;
        }

        String tokenType = jwtTokenProvider.getTokenType(token);
        if (!"temp".equals(tokenType)) {
            throw InvalidTempTokenException.EXCEPTION;
        }

        // 2. JWT에서 정보 추출
        String kakaoId = jwtTokenProvider.getKakaoIdFromToken(token);
        String email = jwtTokenProvider.getEmailFromToken(token);

        // 3. 중복 가입 체크
        if (memberRepository.findByEmail(email).isPresent()) {
            throw DuplicateMemberException.EXCEPTION;
        }

        // 4. 회원 생성 및 저장
        Member newMember = Member.builder()
                .email(email)
                .name(nickname)
                .islandName(islandName)
                .profileImageIndex(profileImageIndex)
                .oauthId(kakaoId)
                .socialInfo("KAKAO")
                .isNew(false)
                .build();

        Member savedMember = memberRepository.save(newMember);

        // 5. 정식 JWT 발급
        String accessToken = jwtTokenProvider.generateAccessToken(savedMember.getId(), savedMember.getEmail());

        log.info("회원가입 완료: memberId = {}, email = {}, nickname = {}", savedMember.getId(), email, nickname);

        return JwtTokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .memberId(savedMember.getId())
                .email(savedMember.getEmail())
                .isNewMember(false)
                .nickname(savedMember.getName())
                .islandName(savedMember.getIslandName())
                .profileImageIndex(savedMember.getProfileImageIndex())
                .build();
    }

    /**
     * 카카오 Access Token으로 사용자 정보 조회
     */
    private KakaoUserResponse getKakaoUserInfo(String accessToken) {
        try {
            return kakaoApiClient.getUserInfo("Bearer " + accessToken);
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage());
            throw KakaoUserInfoFailedException.EXCEPTION;
        }
    }

    /**
     * 구글 Access Token으로 사용자 정보 조회
     */
    private GoogleUserResponse getGoogleUserInfo(String accessToken) {
        try {
            return googleApiClient.getUserInfo("Bearer " + accessToken);
        } catch (Exception e) {
            log.error("구글 사용자 정보 조회 실패: {}", e.getMessage());
            throw GoogleUserInfoFailedException.EXCEPTION;
        }
    }

    @Override
    @Transactional
    public void withdrawMember(Long memberId) {
        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.EXCEPTION);

        // 2. 이미 탈퇴한 회원인지 확인
        if (!member.isActive()) {
            throw MemberAlreadyWithdrawnException.EXCEPTION;
        }

        // 3. 카카오 연결 끊기 (소셜 로그인 연동 해제)
        try {
            String oauthId = member.getOauthId();
            if (oauthId != null && !oauthId.isEmpty()) {
                kakaoApiClient.unlinkUser(
                        "KakaoAK " + kakaoAdminKey,
                        "user_id",
                        oauthId
                );
                log.info("카카오 연결 끊기 성공: memberId = {}, oauthId = {}", memberId, oauthId);
            }
        } catch (Exception e) {
            log.error("카카오 연결 끊기 실패: memberId = {}, error = {}", memberId, e.getMessage());
            throw KakaoUnlinkFailedException.EXCEPTION;
        }

        // 4. 회원 소프트 삭제 (status를 INACTIVE로 변경)
        member.deactivate();

        log.info("회원 탈퇴 완료: memberId = {}, email = {}", memberId, member.getEmail());
    }
}
