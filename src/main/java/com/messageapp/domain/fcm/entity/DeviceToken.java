package com.messageapp.domain.fcm.entity;

import com.messageapp.domain.member.entity.Member;
import com.messageapp.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "device_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "fcm_token", nullable = false, unique = true, length = 500)
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type")
    private DeviceType deviceType;

    @Builder
    public DeviceToken(Member member, String fcmToken, DeviceType deviceType) {
        this.member = member;
        this.fcmToken = fcmToken;
        this.deviceType = deviceType;
    }

    public void updateToken(String newToken) {
        this.fcmToken = newToken;
    }
}
