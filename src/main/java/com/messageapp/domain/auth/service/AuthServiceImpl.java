package com.messageapp.domain.auth.service;

import com.messageapp.api.auth.OauthProvider;
import com.messageapp.domain.auth.dto.JwtTokenResponse;
import com.messageapp.domain.auth.dto.OAuthUserInfo;
import com.messageapp.domain.auth.strategy.OAuthLoginStrategy;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import com.messageapp.global.config.JwtProperties;
import com.messageapp.global.constant.OAuthConstants;
import com.messageapp.global.exception.auth.InvalidAccessTokenException;
import com.messageapp.global.exception.auth.UnsupportedOauthProviderException;
import com.messageapp.global.exception.business.member.DuplicateMemberException;
import com.messageapp.global.exception.business.member.MemberAlreadyWithdrawnException;
import com.messageapp.global.exception.business.member.MemberNotFoundException;
import com.messageapp.global.exception.validation.InvalidTempTokenException;
import com.messageapp.global.jwt.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 인증 서비스 구현체
 *
 * <p>OAuth 소셜 로그인, 회원가입, 회원 탈퇴 기능을 제공합니다.</p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>카카오/구글 소셜 로그인 처리</li>
 *   <li>신규 회원 가입 완료</li>
 *   <li>회원 탈퇴 및 소셜 연결 해제</li>
 * </ul>
 *
 * <h3>Strategy 패턴 적용:</h3>
 * <p>OAuth 제공자별 인증 로직을 {@link OAuthLoginStrategy} 구현체로 분리하여
 * 코드 중복을 제거하고 새로운 OAuth 제공자 추가를 용이하게 합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see OAuthLoginStrategy
 * @see AuthService
 */
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    /** 회원 저장소 */
    private final MemberRepository memberRepository;

    /** JWT 토큰 생성/검증 제공자 */
    private final JwtTokenProvider jwtTokenProvider;

    /** JWT 설정 프로퍼티 */
    private final JwtProperties jwtProperties;

    /** OAuth 연결 해제 서비스 */
    private final OauthUnlinkService oauthUnlinkService;

    /** OAuth 로그인 전략 목록 (Spring에서 자동 주입) */
    private final List<OAuthLoginStrategy> loginStrategies;

    /** OAuth 제공자별 전략 매핑 */
    private Map<OauthProvider, OAuthLoginStrategy> strategyMap;

    /**
     * OAuth 로그인 전략 맵을 초기화합니다.
     *
     * <p>애플리케이션 시작 시 주입된 모든 {@link OAuthLoginStrategy} 구현체를
     * {@link OauthProvider}를 키로 하는 EnumMap에 등록합니다.</p>
     */
    @PostConstruct
    public void initStrategyMap() {
        strategyMap = new EnumMap<>(OauthProvider.class);
        for (OAuthLoginStrategy strategy : loginStrategies) {
            strategyMap.put(strategy.getProvider(), strategy);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public JwtTokenResponse kakaoLogin(String authorizationCode) {
        return socialLogin(authorizationCode, OauthProvider.KAKAO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public JwtTokenResponse googleLogin(String authorizationCode) {
        return socialLogin(authorizationCode, OauthProvider.GOOGLE);
    }

    /**
     * 소셜 로그인 공통 처리 메서드
     *
     * <p>Strategy 패턴을 활용하여 OAuth 제공자별 인증을 처리합니다.</p>
     *
     * <h4>처리 흐름:</h4>
     * <ol>
     *   <li>제공자에 맞는 전략 선택</li>
     *   <li>인가 코드로 사용자 정보 조회</li>
     *   <li>기존 회원이면 Access Token 발급</li>
     *   <li>신규 회원이면 임시 토큰 발급 (회원가입 필요)</li>
     * </ol>
     *
     * @param authorizationCode OAuth 인가 코드
     * @param provider OAuth 제공자 (KAKAO, GOOGLE)
     * @return JWT 토큰 응답 (기존 회원: 정식 토큰, 신규 회원: 임시 토큰)
     * @throws UnsupportedOauthProviderException 지원하지 않는 OAuth 제공자인 경우
     */
    private JwtTokenResponse socialLogin(String authorizationCode, OauthProvider provider) {
        OAuthLoginStrategy strategy = strategyMap.get(provider);
        if (strategy == null) {
            throw new UnsupportedOauthProviderException();
        }

        OAuthUserInfo userInfo = strategy.authenticate(authorizationCode);
        String virtualEmail = userInfo.getEmail();
        String oauthId = userInfo.getOauthId();

        Member existingMember = memberRepository.findByEmail(virtualEmail).orElse(null);

        if (existingMember != null) {
            String accessToken = jwtTokenProvider.generateAccessToken(existingMember.getId(), existingMember.getEmail());
            log.info("기존 회원 로그인 성공: memberId = {}, email = {}", existingMember.getId(), virtualEmail);

            return JwtTokenResponse.builder()
                    .accessToken(accessToken)
                    .tokenType(OAuthConstants.TOKEN_TYPE_BEARER)
                    .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                    .memberId(existingMember.getId())
                    .email(existingMember.getEmail())
                    .isNewMember(false)
                    .nickname(existingMember.getName())
                    .islandName(existingMember.getIslandName())
                    .profileImageIndex(existingMember.getProfileImageIndex())
                    .build();
        } else {
            String tempAccessToken = jwtTokenProvider.generateTempAccessToken(oauthId, virtualEmail, provider);
            log.info("신규 회원 {} 인증 성공: oauthId = {}, email = {}", provider.getValue(), oauthId, virtualEmail);

            return JwtTokenResponse.builder()
                    .accessToken(tempAccessToken)
                    .tokenType(OAuthConstants.TOKEN_TYPE_BEARER)
                    .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                    .memberId(null)
                    .email(virtualEmail)
                    .isNewMember(true)
                    .build();
        }
    }

    /**
     * {@inheritDoc}
     *
     * <h4>처리 흐름:</h4>
     * <ol>
     *   <li>임시 토큰 유효성 검증</li>
     *   <li>토큰에서 OAuth 정보 추출 (oauthId, provider, email)</li>
     *   <li>이메일 중복 확인</li>
     *   <li>신규 회원 생성 및 저장</li>
     *   <li>정식 Access Token 발급</li>
     * </ol>
     */
    @Override
    @Transactional
    public JwtTokenResponse completeSignup(String token, String nickname, String islandName, Integer profileImageIndex) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidAccessTokenException();
        }

        String tokenType = jwtTokenProvider.getTokenType(token);
        if (!OAuthConstants.TOKEN_TYPE_TEMP.equals(tokenType)) {
            throw new InvalidTempTokenException();
        }

        String oauthId = jwtTokenProvider.getOauthIdFromToken(token);
        OauthProvider oauthProvider = jwtTokenProvider.getOauthProviderFromToken(token);
        String email = jwtTokenProvider.getEmailFromToken(token);

        if (memberRepository.findByEmail(email).isPresent()) {
            throw new DuplicateMemberException();
        }

        Member newMember = Member.builder()
                .email(email)
                .name(nickname)
                .islandName(islandName)
                .profileImageIndex(profileImageIndex)
                .oauthId(oauthId)
                .socialInfo(oauthProvider.getValue())
                .isNew(false)
                .build();

        Member savedMember = memberRepository.save(newMember);
        String accessToken = jwtTokenProvider.generateAccessToken(savedMember.getId(), savedMember.getEmail());

        log.info("회원가입 완료: memberId = {}, email = {}, nickname = {}", savedMember.getId(), email, nickname);

        return JwtTokenResponse.builder()
                .accessToken(accessToken)
                .tokenType(OAuthConstants.TOKEN_TYPE_BEARER)
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
     * {@inheritDoc}
     *
     * <h4>처리 흐름:</h4>
     * <ol>
     *   <li>회원 조회 및 존재 여부 확인</li>
     *   <li>이미 탈퇴한 회원인지 확인</li>
     *   <li>OAuth 연결 해제 (카카오/구글 연결 끊기)</li>
     *   <li>회원 상태를 INACTIVE로 변경 (소프트 삭제)</li>
     * </ol>
     */
    @Override
    @Transactional
    public void withdrawMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        if (!member.isActive()) {
            throw new MemberAlreadyWithdrawnException();
        }

        String oauthId = member.getOauthId();
        if (oauthId != null && !oauthId.isEmpty()) {
            OauthProvider provider = OauthProvider.fromString(member.getSocialInfo());
            oauthUnlinkService.unlink(provider, oauthId);
            log.info("소셜 로그인 연결 해제 성공: memberId = {}, provider = {}, oauthId = {}",
                    memberId, provider.getValue(), oauthId);
        }

        member.deactivate();
        log.info("회원 탈퇴 완료: memberId = {}, email = {}", memberId, member.getEmail());
    }
}
