package com.messageapp.domain.letter.repository;

import com.messageapp.domain.letter.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {

    /**
     * 특정 수신자가 받은 편지 목록 조회 (최신순)
     */
    List<Letter> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
}
