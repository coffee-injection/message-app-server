package com.messageapp.domain.letter.repository;

import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.entity.LetterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * @deprecated 차단 기능 도입으로 {@link #findByReceiverIdAndStatusExcludingBlockedSenders} 사용 권장
     */
    @Deprecated
    List<Letter> findByReceiverIdAndStatusOrderByCreatedAtDesc(Long receiverId, LetterStatus status);

    /**
     * 특정 수신자가 받은 특정 상태의 편지 목록을 최신순으로 조회합니다.
     * 수신자가 차단한 발신자의 편지는 제외됩니다.
     *
     * @param receiverId 수신자 ID
     * @param status 편지 상태
     * @return 편지 목록 (최신순, 차단한 발신자 제외)
     */
    @Query(value = """
            SELECT l.* FROM letters l
            WHERE l.receiver_id = :receiverId
              AND l.status = :#{#status.name()}
              AND l.sender_id NOT IN (
                  SELECT mb.blocked_id FROM member_blocks mb WHERE mb.blocker_id = :receiverId
              )
            ORDER BY l.created_at DESC
            """, nativeQuery = true)
    List<Letter> findByReceiverIdAndStatusExcludingBlockedSenders(
            @Param("receiverId") Long receiverId,
            @Param("status") LetterStatus status);
}
