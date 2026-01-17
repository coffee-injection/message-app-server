package com.messageapp.domain.fcm.dto;

import com.messageapp.domain.fcm.entity.DeviceType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeviceTokenRequest {

    @NotBlank(message = "FCM 토큰은 필수입니다.")
    private String fcmToken;

    private DeviceType deviceType;

    public DeviceTokenRequest(String fcmToken, DeviceType deviceType) {
        this.fcmToken = fcmToken;
        this.deviceType = deviceType;
    }
}
