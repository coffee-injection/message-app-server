package com.messageapp.domain.auth.entity;

import com.messageapp.domain.member.entity.Member;
import com.messageapp.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Refresh Token 엔티티
 *
 * <p>Access Token 갱신을 위한 Refresh Token 정보를 저장합니다.</p>
 *
 * <h3>특징:</h3>
 * <ul>
 *   <li>회원당 하나의 Refresh Token만 유지 (1:1 관계)</li>
 *   <li>만료 시간이 지난 토큰은 사용 불가</li>
 *   <li>회원 탈퇴 시 함께 삭제</li>
 * </ul>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see Member
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    /** 토큰 고유 식별자 (PK, Auto Increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 연결된 회원 (1:1 관계) */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    /** Refresh Token 값 */
    @Column(nullable = false, unique = true, length = 512)
    private String token;

    /** 토큰 만료 시간 */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Refresh Token 생성자
     *
     * @param member 연결할 회원
     * @param token 토큰 값
     * @param expiresAt 만료 시간
     */
    @Builder
    public RefreshToken(Member member, String token, LocalDateTime expiresAt) {
        this.member = member;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    /**
     * 토큰 값을 갱신합니다 (Refresh Token Rotation).
     *
     * @param newToken 새로운 토큰 값
     * @param newExpiresAt 새로운 만료 시간
     */
    public void updateToken(String newToken, LocalDateTime newExpiresAt) {
        this.token = newToken;
        this.expiresAt = newExpiresAt;
    }

    /**
     * 토큰이 만료되었는지 확인합니다.
     *
     * @return 만료되었으면 true
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
