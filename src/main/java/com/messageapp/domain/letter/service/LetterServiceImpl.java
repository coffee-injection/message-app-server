package com.messageapp.domain.letter.service;

import com.messageapp.domain.letter.dto.LetterResponse;
import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.repository.LetterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LetterServiceImpl implements LetterService {

    private final LetterRepository letterRepository;

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
}
