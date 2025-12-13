package com.messageapp.domain.bookmark.repository;

import com.messageapp.domain.bookmark.entity.Bookmark;
import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * 중복 북마크 여부 확인
     */
    boolean existsByMemberIdAndLetterId(Long memberId, Long letterId);

    /**
     * 특정 회원의 특정 편지 북마크 조회
     */
    Optional<Bookmark> findByMemberAndLetter(Member member, Letter letter);

    /**
     * 특정 회원의 북마크 목록 조회 (생성일 내림차순)
     */
    List<Bookmark> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}
