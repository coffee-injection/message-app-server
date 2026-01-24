package com.messageapp.domain.bookmark.service;

import com.messageapp.domain.bookmark.entity.Bookmark;
import com.messageapp.domain.bookmark.repository.BookmarkRepository;
import com.messageapp.domain.letter.dto.LetterResponse;
import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.repository.LetterRepository;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import com.messageapp.global.exception.business.bookmark.BookmarkAccessDeniedException;
import com.messageapp.global.exception.business.bookmark.BookmarkNotFoundException;
import com.messageapp.global.exception.business.bookmark.DuplicateBookmarkException;
import com.messageapp.global.exception.business.letter.LetterNotFoundException;
import com.messageapp.global.exception.business.member.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final LetterRepository letterRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void saveLetter(Long memberId, Long letterId) {
        // 1. 편지 조회
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(LetterNotFoundException::new);

        // 2. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // 3. 수신자 확인 (본인에게 수신된 편지만 북마크 가능)
        if (letter.getReceiver() == null || !letter.getReceiver().getId().equals(memberId)) {
            throw new BookmarkAccessDeniedException();
        }

        // 4. 중복 북마크 체크
        if (bookmarkRepository.existsByMemberIdAndLetterId(memberId, letterId)) {
            throw new DuplicateBookmarkException();
        }

        // 5. 북마크 생성 및 저장
        Bookmark bookmark = Bookmark.builder()
                .letter(letter)
                .member(member)
                .build();

        bookmarkRepository.save(bookmark);

        log.info("북마크 저장 완료: memberId = {}, letterId = {}", memberId, letterId);
    }

    @Override
    public List<LetterResponse> getBookmarkList(Long memberId) {
        // 1. memberId로 북마크 목록 조회
        List<Bookmark> bookmarks = bookmarkRepository.findByMemberIdOrderByCreatedAtDesc(memberId);

        // 2. Bookmark -> Letter -> LetterResponse 변환
        return bookmarks.stream()
                .map(Bookmark::getLetter)
                .map(LetterResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void deleteLetter(Long memberId, Long letterId) {
        // 1. 북마크 존재 확인
        if (!bookmarkRepository.existsByMemberIdAndLetterId(memberId, letterId)) {
            throw new BookmarkNotFoundException();
        }

        // 2. 북마크 삭제
        bookmarkRepository.deleteByMemberIdAndLetterId(memberId, letterId);

        log.info("북마크 삭제 완료: memberId = {}, letterId = {}", memberId, letterId);
    }
}
