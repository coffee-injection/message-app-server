package com.messageapp.domain.letter.entity;

import com.messageapp.domain.member.entity.Member;
import com.messageapp.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 편지 엔티티
 *
 * <p>사용자 간 익명 편지를 관리하는 핵심 도메인 엔티티입니다.</p>
 *
 * <h3>편지 상태 (LetterStatus):</h3>
 * <ul>
 *   <li>WAITING: 대기 중 (수신자 미배정)</li>
 *   <li>DELIVERED: 배달 완료 (수신자 배정됨, 미읽음)</li>
 *   <li>READ: 읽음</li>
 * </ul>
 *
 * <h3>편지 라이프사이클:</h3>
 * <ol>
 *   <li>발신자가 편지 작성 → WAITING 상태로 생성</li>
 *   <li>랜덤 수신자 배정 → DELIVERED 상태로 변경</li>
 *   <li>수신자가 편지 열람 → READ 상태로 변경</li>
 * </ol>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see LetterStatus
 */
@Entity
@Table(name = "letters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Letter extends BaseEntity {

    /** 편지 고유 식별자 (PK, Auto Increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "letter_id")
    private Long id;

    /** 발신자 (필수) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    /** 수신자 (배정 전에는 null) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    /** 편지 내용 */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 편지 상태 (WAITING, DELIVERED, READ) */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LetterStatus status = LetterStatus.WAITING;

    /** 수신자 배정 시각 */
    @Column(name = "matched_at")
    private LocalDateTime matchedAt;

    /** 읽음 처리 시각 */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * 편지 생성자
     *
     * <p>초기 상태는 WAITING이며, 수신자는 추후 배정됩니다.</p>
     *
     * @param sender 발신자
     * @param content 편지 내용
     */
    @Builder
    public Letter(Member sender, String content) {
        this.sender = sender;
        this.content = content;
        this.status = LetterStatus.WAITING;
    }

    // ==================== 비즈니스 메서드 ====================

    /**
     * 수신자를 배정합니다.
     *
     * <p>상태를 DELIVERED로 변경하고 배정 시각을 기록합니다.</p>
     *
     * @param receiver 배정할 수신자
     */
    public void assignReceiver(Member receiver) {
        this.receiver = receiver;
        this.status = LetterStatus.DELIVERED;
        this.matchedAt = LocalDateTime.now();
    }

    /**
     * 편지를 읽음 상태로 변경합니다.
     *
     * <p>DELIVERED 상태인 경우에만 READ로 변경됩니다.</p>
     */
    public void markAsRead() {
        if (this.status == LetterStatus.DELIVERED) {
            this.status = LetterStatus.READ;
            this.readAt = LocalDateTime.now();
        }
    }

    /**
     * 대기 중인 편지인지 확인합니다.
     *
     * @return WAITING 상태이면 true
     */
    public boolean isWaiting() {
        return this.status == LetterStatus.WAITING;
    }

    /**
     * 배달 완료된 편지인지 확인합니다.
     *
     * @return DELIVERED 상태이면 true
     */
    public boolean isDelivered() {
        return this.status == LetterStatus.DELIVERED;
    }
}
