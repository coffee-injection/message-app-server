package com.messageapp.domain.letter.repository;

import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.entity.LetterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 편지 저장소
 *
 * <p>편지 엔티티에 대한 데이터 접근을 제공합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see Letter
 */
@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {

    /**
     * 특정 수신자가 받은 편지 목록을 최신순으로 조회합니다.
     *
     * @param receiverId 수신자 ID
     * @return 편지 목록 (최신순)
     */
    List<Letter> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    /**
     * 특정 수신자가 받은 특정 상태의 편지 목록을 최신순으로 조회합니다.
     *
     * <p>주로 DELIVERED 상태(배달 완료, 미읽음) 편지를 조회하는 데 사용됩니다.</p>
     *
     * @param receiverId 수신자 ID
     * @param status 편지 상태
     * @return 편지 목록 (최신순)
     */
    List<Letter> findByReceiverIdAndStatusOrderByCreatedAtDesc(Long receiverId, LetterStatus status);
}
