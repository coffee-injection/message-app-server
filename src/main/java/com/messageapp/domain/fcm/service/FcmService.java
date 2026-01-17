package com.messageapp.domain.fcm.service;

import com.messageapp.domain.fcm.dto.DeviceTokenRequest;

public interface FcmService {

    void registerToken(Long memberId, DeviceTokenRequest request);

    void removeToken(Long memberId, String fcmToken);

    void sendNotification(Long receiverId, String title, String body);

    void sendLetterArrivalNotification(Long receiverId, String senderName);
}
