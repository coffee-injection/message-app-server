package com.messageapp.domain.letter.service;

import com.messageapp.domain.letter.dto.LetterResponse;
import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.repository.LetterRepository;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
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
    @Transactional
    public List<LetterResponse> getReceivedLetters(Long memberId) {
        List<Letter> letters = letterRepository.findByReceiverIdOrderByCreatedAtDesc(memberId);

        // 각 편지를 읽음 상태로 변경 (DELIVERED -> READ)
        letters.forEach(Letter::markAsRead);

        log.info("수신 편지 목록 조회 및 읽음 처리: memberId = {}, count = {}", memberId, letters.size());

        return letters.stream()
                .map(LetterResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public LetterResponse sendLetter(Long senderId, String content) {
        // 1. 발신자 조회
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("발신자를 찾을 수 없습니다."));

        // 2. 발신자를 제외한 활성 회원 목록 조회
        List<Member> activeMembers = memberRepository.findActiveMembers(senderId);

        if (activeMembers.isEmpty()) {
            throw new RuntimeException("편지를 받을 수 있는 회원이 없습니다.");
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
}
