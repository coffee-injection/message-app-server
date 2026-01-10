package com.messageapp.domain.letter.service;

import com.messageapp.domain.letter.dto.LetterIdResponse;
import com.messageapp.domain.letter.dto.LetterResponse;
import com.messageapp.domain.letter.entity.Letter;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LetterServiceImpl implements LetterService {

    private final LetterRepository letterRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<LetterIdResponse> getReceivedLetters(Long memberId) {
        List<Letter> letters = letterRepository.findByReceiverIdOrderByCreatedAtDesc(memberId);

        log.info("수신 편지 목록 조회: memberId = {}, count = {}", memberId, letters.size());

        return letters.stream()
                .map(LetterIdResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public LetterResponse sendLetter(Long senderId, String content) {
        // 1. 발신자 조회
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> SenderNotFoundException.EXCEPTION);

        // 2. 발신자를 제외한 활성 회원 목록 조회
        List<Member> activeMembers = memberRepository.findActiveMembers(senderId);

        if (activeMembers.isEmpty()) {
            throw NoAvailableReceiverException.EXCEPTION;
        }

        // 3. 랜덤으로 수신자 선택 (ThreadLocalRandom으로 동시성 안전)
        Member receiver = activeMembers.get(ThreadLocalRandom.current().nextInt(activeMembers.size()));

        // 4. 편지 생성 및 수신자 할당
        Letter letter = Letter.builder()
                .sender(sender)
                .content(content)
                .build();

        letter.assignReceiver(receiver);

        // 5. 편지 저장
        Letter savedLetter = letterRepository.save(letter);

        log.info("편지 발송 완료: senderId = {}, receiverId = {}, letterId = {}",
                senderId, receiver.getId(), savedLetter.getId());

        return LetterResponse.from(savedLetter);
    }

    @Override
    @Transactional
    public LetterResponse getLetterDetail(Long letterId, Long memberId) {
        // 1. 편지 조회
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> LetterNotFoundException.EXCEPTION);

        // 2. 수신자 권한 확인 (본인의 편지만 조회 가능)
        if (letter.getReceiver() == null || !letter.getReceiver().getId().equals(memberId)) {
            throw LetterAccessDeniedException.EXCEPTION;
        }

        // 3. 편지를 읽음 상태로 변경
        letter.markAsRead();

        log.info("편지 상세 조회: letterId = {}, memberId = {}", letterId, memberId);

        return LetterResponse.from(letter);
    }
}
