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

    @Column(name = "island_name", length = 8)
    private String islandName;

    private String oauthId;

    private Boolean isNew;

    @Column(name = "profile_image_index")
    private Integer profileImageIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Builder
    public Member(String socialInfo, String email, String name, String islandName, String oauthId, Boolean isNew, Integer profileImageIndex) {
        this.socialInfo = socialInfo;
        this.email = email;
        this.name = name;
        this.islandName = islandName;
        this.oauthId = oauthId;
        this.isNew = isNew;
        this.profileImageIndex = profileImageIndex;
        this.status = MemberStatus.ACTIVE;
    }

    // 비즈니스 메서드
    public void updateName(String name) {
        this.name = name;
    }

    public void updateIslandName(String islandName) {
        if (islandName != null && (islandName.length() < 2 || islandName.length() > 8)) {
            throw new IllegalArgumentException("섬 이름은 2~8글자여야 합니다.");
        }
        this.islandName = islandName;
    }

    public void updateProfileImageIndex(Integer profileImageIndex) {
        if (profileImageIndex != null && (profileImageIndex < 1 || profileImageIndex > 21)) {
            throw new IllegalArgumentException("프로필 이미지 인덱스는 1~12 범위여야 합니다.");
        }
        this.profileImageIndex = profileImageIndex;
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
