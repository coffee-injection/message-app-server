package com.messageapp.domain.letter.repository;

import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.entity.LetterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {

    /**
     * 특정 수신자가 받은 편지 목록 조회 (최신순)
     */
    List<Letter> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    /**
     * 특정 수신자가 받은 특정 상태의 편지 목록 조회 (최신순)
     */
    List<Letter> findByReceiverIdAndStatusOrderByCreatedAtDesc(Long receiverId, LetterStatus status);
}
