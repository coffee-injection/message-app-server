package com.messageapp.domain.letter.entity;

import com.messageapp.domain.member.entity.Member;
import com.messageapp.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "letters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Letter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "letter_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LetterStatus status = LetterStatus.WAITING;

    @Column(name = "matched_at")
    private LocalDateTime matchedAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Builder
    public Letter(Member sender, String content) {
        this.sender = sender;
        this.content = content;
        this.status = LetterStatus.WAITING;
    }

    // 비즈니스 메서드
    public void assignReceiver(Member receiver) {
        this.receiver = receiver;
        this.status = LetterStatus.DELIVERED;
        this.matchedAt = LocalDateTime.now();
    }

    public void markAsRead() {
        if (this.status == LetterStatus.DELIVERED) {
            this.status = LetterStatus.READ;
            this.readAt = LocalDateTime.now();
        }
    }

    public boolean isWaiting() {
        return this.status == LetterStatus.WAITING;
    }

    public boolean isDelivered() {
        return this.status == LetterStatus.DELIVERED;
    }
}
