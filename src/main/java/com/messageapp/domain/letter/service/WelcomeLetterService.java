package com.messageapp.domain.letter.service;

import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.repository.LetterRepository;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 환영 편지 서비스
 *
 * <p>신규 회원에게 환영 편지를 발송합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WelcomeLetterService {

    private static final String SYSTEM_EMAIL = "system@tium.app";
    private static final String SYSTEM_NAME = "띄움";
    private static final String SYSTEM_ISLAND_NAME = "띄움섬";
    private static final String SYSTEM_OAUTH_ID = "SYSTEM";
    private static final String SYSTEM_SOCIAL_INFO = "SYSTEM";

    private static final String WELCOME_MESSAGE = """
            띄움에 오신 걸 환영합니다 :)
            지금 이 순간, 누군가가 당신의 편지를 기다리고 있을지도 몰라요.
            편지는 익명으로 전송되며, 단 한 명에게만 도착합니다.""";

    private final MemberRepository memberRepository;
    private final LetterRepository letterRepository;

    /**
     * 신규 회원에게 환영 편지를 발송합니다.
     *
     * @param newMember 신규 가입한 회원
     */
    @Transactional
    public void sendWelcomeLetter(Member newMember) {
        Member systemMember = getOrCreateSystemMember();

        Letter welcomeLetter = Letter.builder()
                .sender(systemMember)
                .content(WELCOME_MESSAGE)
                .build();

        // 수신자 직접 배정 (DELIVERED 상태로 변경)
        welcomeLetter.assignReceiver(newMember);

        letterRepository.save(welcomeLetter);

        log.info("환영 편지 발송 완료: receiverId = {}", newMember.getId());
    }

    /**
     * 시스템 회원을 조회하거나 없으면 생성합니다.
     *
     * @return 시스템 회원
     */
    private Member getOrCreateSystemMember() {
        return memberRepository.findByEmail(SYSTEM_EMAIL)
                .orElseGet(this::createSystemMember);
    }

    /**
     * 시스템 회원을 생성합니다.
     *
     * @return 생성된 시스템 회원
     */
    private Member createSystemMember() {
        Member systemMember = Member.builder()
                .email(SYSTEM_EMAIL)
                .name(SYSTEM_NAME)
                .islandName(SYSTEM_ISLAND_NAME)
                .profileImageIndex(1)
                .oauthId(SYSTEM_OAUTH_ID)
                .socialInfo(SYSTEM_SOCIAL_INFO)
                .isNew(false)
                .build();

        Member saved = memberRepository.save(systemMember);
        log.info("시스템 회원 생성 완료: memberId = {}", saved.getId());

        return saved;
    }
}
