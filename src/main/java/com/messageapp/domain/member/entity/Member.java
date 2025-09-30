package com.messageapp.domain.member.entity;

import com.messageapp.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "social_info")
    private String socialInfo;

    @Column(name = "member_email", nullable = false, unique = true)
    private String email;

    @Column(name = "member_name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Builder
    public Member(String socialInfo, String email, String name) {
        this.socialInfo = socialInfo;
        this.email = email;
        this.name = name;
        this.status = MemberStatus.ACTIVE;
    }

    // 비즈니스 메서드
    public void updateName(String name) {
        this.name = name;
    }

    public void deactivate() {
        this.status = MemberStatus.INACTIVE;
    }

    public void ban() {
        this.status = MemberStatus.BANNED;
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }
}
