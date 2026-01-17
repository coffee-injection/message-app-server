package com.messageapp.domain.fcm.repository;

import com.messageapp.domain.fcm.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    List<DeviceToken> findByMemberId(Long memberId);

    Optional<DeviceToken> findByFcmToken(String fcmToken);

    boolean existsByFcmToken(String fcmToken);

    void deleteByMemberIdAndFcmToken(Long memberId, String fcmToken);

    void deleteByFcmToken(String fcmToken);
}
