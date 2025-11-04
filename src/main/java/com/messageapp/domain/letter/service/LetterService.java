package com.messageapp.domain.letter.service;

import com.messageapp.domain.letter.dto.LetterResponse;

import java.util.List;

public interface LetterService {

    /**
     * 수신한 편지 목록 조회
     */
    List<LetterResponse> getReceivedLetters(Long memberId);

    /**
     * 편지 발송 (랜덤 수신자 매칭)
     */
    LetterResponse sendLetter(Long senderId, String content);
}
