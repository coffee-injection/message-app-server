package com.messageapp.domain.block.entity;

import com.messageapp.domain.member.entity.Member;
import com.messageapp.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 차단 엔티티
 *
 * <p>회원 간 차단 관계를 저장합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Entity
@Table(name = "member_blocks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"blocker_id", "blocked_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberBlock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id")
    private Long id;

    /** 차단한 회원 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    private Member blocker;

    /** 차단당한 회원 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", nullable = false)
    private Member blocked;

    @Builder
    public MemberBlock(Member blocker, Member blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
    }
}
