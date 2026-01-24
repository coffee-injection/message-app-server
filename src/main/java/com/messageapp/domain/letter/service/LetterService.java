package com.messageapp.domain.letter.service;

import com.messageapp.domain.letter.dto.LetterIdResponse;
import com.messageapp.domain.letter.dto.LetterResponse;
import com.messageapp.global.exception.business.letter.LetterAccessDeniedException;
import com.messageapp.global.exception.business.letter.LetterNotFoundException;
import com.messageapp.global.exception.business.letter.NoAvailableReceiverException;
import com.messageapp.global.exception.business.letter.SenderNotFoundException;

import java.util.List;

/**
 * 편지 서비스 인터페이스
 *
 * <p>편지 발송, 조회, 읽음 처리 기능을 정의합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
public interface LetterService {

    /**
     * 수신한 편지 목록을 조회합니다.
     *
     * <p>배달 완료(DELIVERED) 상태의 편지만 조회됩니다.</p>
     *
     * @param memberId 조회할 회원 ID
     * @return 편지 ID 목록 (최신순)
     */
    List<LetterIdResponse> getReceivedLetters(Long memberId);

    /**
     * 편지를 발송합니다.
     *
     * <p>시스템이 자동으로 활성 회원 중 랜덤한 수신자를 선택합니다.
     * 발신자 자신은 수신자에서 제외됩니다.</p>
     *
     * @param senderId 발신자 ID
     * @param content 편지 내용
     * @return 발송된 편지 정보
     * @throws SenderNotFoundException 발신자를 찾을 수 없는 경우
     * @throws NoAvailableReceiverException 배정 가능한 수신자가 없는 경우
     */
    LetterResponse sendLetter(Long senderId, String content);

    /**
     * 편지 상세 정보를 조회합니다.
     *
     * <p>수신자만 조회할 수 있으며, 조회 시 자동으로 읽음 처리됩니다.</p>
     *
     * @param letterId 편지 ID
     * @param memberId 조회하는 회원 ID
     * @return 편지 상세 정보
     * @throws LetterNotFoundException 편지를 찾을 수 없는 경우
     * @throws LetterAccessDeniedException 수신자가 아닌 경우
     */
    LetterResponse getLetterDetail(Long letterId, Long memberId);
}
