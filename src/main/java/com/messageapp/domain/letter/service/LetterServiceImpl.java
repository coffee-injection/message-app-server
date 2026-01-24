package com.messageapp.domain.letter.service;

import com.messageapp.domain.fcm.service.FcmService;
import com.messageapp.domain.letter.dto.LetterIdResponse;
import com.messageapp.domain.letter.dto.LetterResponse;
import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.entity.LetterStatus;
import com.messageapp.domain.letter.repository.LetterRepository;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import com.messageapp.global.exception.business.letter.LetterAccessDeniedException;
import com.messageapp.global.exception.business.letter.LetterNotFoundException;
import com.messageapp.global.exception.business.letter.NoAvailableReceiverException;
import com.messageapp.global.exception.business.letter.SenderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 편지 서비스 구현체
 *
 * <p>편지 발송, 조회, 읽음 처리 기능을 제공합니다.</p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>수신 편지 목록 조회</li>
 *   <li>편지 발송 (랜덤 수신자 배정)</li>
 *   <li>편지 상세 조회 및 읽음 처리</li>
 * </ul>
 *
 * <h3>편지 발송 플로우:</h3>
 * <p>발신자가 편지를 작성하면, 시스템이 자동으로 랜덤한 활성 회원을
 * 수신자로 배정하고 FCM 푸시 알림을 발송합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see LetterService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LetterServiceImpl implements LetterService {

    /** 편지 저장소 */
    private final LetterRepository letterRepository;

    /** 회원 저장소 */
    private final MemberRepository memberRepository;

    /** FCM 푸시 알림 서비스 */
    private final FcmService fcmService;

    /**
     * {@inheritDoc}
     *
     * <p>배달 완료(DELIVERED) 상태이고 아직 읽지 않은 편지 목록을
     * 최신순으로 조회합니다.</p>
     */
    @Override
    public List<LetterIdResponse> getReceivedLetters(Long memberId) {
        List<Letter> letters = letterRepository.findByReceiverIdAndStatusOrderByCreatedAtDesc(
                memberId, LetterStatus.DELIVERED);

        log.info("읽지 않은 수신 편지 목록 조회: memberId = {}, count = {}", memberId, letters.size());

        return letters.stream()
                .map(LetterIdResponse::from)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <h4>처리 흐름:</h4>
     * <ol>
     *   <li>발신자 조회</li>
     *   <li>DB에서 랜덤 수신자 선택 (발신자 제외, 활성 회원만)</li>
     *   <li>편지 생성 및 수신자 배정</li>
     *   <li>편지 저장</li>
     *   <li>수신자에게 FCM 푸시 알림 발송</li>
     * </ol>
     *
     * <p>랜덤 수신자 선택은 DB 네이티브 쿼리(ORDER BY RAND())를 사용하여
     * 메모리 사용을 최소화합니다.</p>
     */
    @Override
    @Transactional
    public LetterResponse sendLetter(Long senderId, String content) {
        // 발신자 조회
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(SenderNotFoundException::new);

        // DB에서 랜덤 수신자 선택 (성능 최적화: ORDER BY RAND() LIMIT 1)
        Member receiver = memberRepository.findRandomActiveMember(senderId)
                .orElseThrow(NoAvailableReceiverException::new);

        // 편지 생성 및 수신자 배정
        Letter letter = Letter.builder()
                .sender(sender)
                .content(content)
                .build();

        letter.assignReceiver(receiver);

        Letter savedLetter = letterRepository.save(letter);

        log.info("편지 발송 완료: senderId = {}, receiverId = {}, letterId = {}",
                senderId, receiver.getId(), savedLetter.getId());

        // FCM 푸시 알림 발송 (비동기)
        fcmService.sendLetterArrivalNotification(receiver.getId(), sender.getName());

        return LetterResponse.from(savedLetter);
    }

    /**
     * {@inheritDoc}
     *
     * <h4>처리 흐름:</h4>
     * <ol>
     *   <li>편지 조회</li>
     *   <li>수신자 권한 확인 (본인 편지만 열람 가능)</li>
     *   <li>편지 상태를 READ로 변경</li>
     * </ol>
     *
     * <p>수신자가 아닌 회원이 조회를 시도하면 {@link LetterAccessDeniedException}이
     * 발생합니다.</p>
     */
    @Override
    @Transactional
    public LetterResponse getLetterDetail(Long letterId, Long memberId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(LetterNotFoundException::new);

        // 수신자 권한 확인 (본인의 편지만 조회 가능)
        if (letter.getReceiver() == null || !letter.getReceiver().getId().equals(memberId)) {
            throw new LetterAccessDeniedException();
        }

        // 편지를 읽음 상태로 변경
        letter.markAsRead();

        log.info("편지 상세 조회: letterId = {}, memberId = {}", letterId, memberId);

        return LetterResponse.from(letter);
    }
}
