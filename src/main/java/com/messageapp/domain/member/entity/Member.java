package com.messageapp.domain.member.entity;

import com.messageapp.global.common.BaseEntity;
import com.messageapp.global.constant.MemberConstants;
import com.messageapp.global.exception.validation.InvalidIslandNameException;
import com.messageapp.global.exception.validation.InvalidProfileImageIndexException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 엔티티
 *
 * <p>서비스의 회원 정보를 관리하는 핵심 도메인 엔티티입니다.</p>
 *
 * <h3>회원 상태 (MemberStatus):</h3>
 * <ul>
 *   <li>ACTIVE: 활성 회원</li>
 *   <li>INACTIVE: 탈퇴한 회원 (소프트 삭제)</li>
 *   <li>BANNED: 차단된 회원</li>
 * </ul>
 *
 * <h3>필드 제약조건:</h3>
 * <ul>
 *   <li>닉네임: 2~10자</li>
 *   <li>섬 이름: 1~8자</li>
 *   <li>프로필 이미지 인덱스: 1~21</li>
 * </ul>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see MemberStatus
 * @see MemberConstants
 */
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    /** 회원 고유 식별자 (PK, Auto Increment) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    /** 소셜 로그인 제공자 정보 (kakao, google 등) */
    @Column(name = "social_info")
    private String socialInfo;

    /** 회원 이메일 (가상 이메일: {provider}_{oauthId}@virtual.com) */
    @Column(name = "member_email", nullable = false, unique = true)
    private String email;

    /** 회원 닉네임 (2~10자) */
    @Column(name = "member_name", nullable = false, length = 10)
    private String name;

    /** 회원의 섬 이름 (1~8자) */
    @Column(name = "island_name", length = 8)
    private String islandName;

    /** OAuth 제공자의 고유 사용자 ID */
    private String oauthId;

    /** 신규 회원 여부 (현재 미사용) */
    private Boolean isNew;

    /** 프로필 이미지 인덱스 (1~21) */
    @Column(name = "profile_image_index")
    private Integer profileImageIndex;

    /** 회원 상태 (ACTIVE, INACTIVE, BANNED) */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    /**
     * 회원 생성자
     *
     * @param socialInfo 소셜 로그인 제공자
     * @param email 회원 이메일
     * @param name 닉네임
     * @param islandName 섬 이름
     * @param oauthId OAuth 고유 ID
     * @param isNew 신규 회원 여부
     * @param profileImageIndex 프로필 이미지 인덱스
     */
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

    // ==================== 비즈니스 메서드 ====================

    /**
     * 닉네임을 변경합니다.
     *
     * @param name 새로운 닉네임
     */
    public void updateName(String name) {
        this.name = name;
    }

    /**
     * 섬 이름을 변경합니다.
     *
     * @param islandName 새로운 섬 이름 (1~8자)
     * @throws InvalidIslandNameException 유효하지 않은 길이인 경우
     */
    public void updateIslandName(String islandName) {
        if (islandName != null && (islandName.length() < MemberConstants.ISLAND_NAME_MIN_LENGTH
                || islandName.length() > MemberConstants.ISLAND_NAME_MAX_LENGTH)) {
            throw new InvalidIslandNameException();
        }
        this.islandName = islandName;
    }

    /**
     * 프로필 이미지 인덱스를 변경합니다.
     *
     * @param profileImageIndex 새로운 프로필 이미지 인덱스 (1~21)
     * @throws InvalidProfileImageIndexException 유효하지 않은 범위인 경우
     */
    public void updateProfileImageIndex(Integer profileImageIndex) {
        if (profileImageIndex != null && (profileImageIndex < MemberConstants.PROFILE_IMAGE_MIN_INDEX
                || profileImageIndex > MemberConstants.PROFILE_IMAGE_MAX_INDEX)) {
            throw new InvalidProfileImageIndexException();
        }
        this.profileImageIndex = profileImageIndex;
    }

    /**
     * 회원을 비활성화합니다 (탈퇴 처리).
     *
     * <p>상태를 INACTIVE로 변경하여 소프트 삭제를 수행합니다.</p>
     */
    public void deactivate() {
        this.status = MemberStatus.INACTIVE;
    }

    /**
     * 회원을 차단합니다.
     *
     * <p>상태를 BANNED로 변경합니다.</p>
     */
    public void ban() {
        this.status = MemberStatus.BANNED;
    }

    /**
     * 회원이 활성 상태인지 확인합니다.
     *
     * @return 활성 상태이면 true
     */
    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }
}
