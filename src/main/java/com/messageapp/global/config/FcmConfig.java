package com.messageapp.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Slf4j
@Configuration
public class FcmConfig {

    @Value("${fcm.service-account-file}")
    private Resource serviceAccountResource;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(
                                serviceAccountResource.getInputStream()))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase Admin SDK 초기화 완료");
            }
        } catch (IOException e) {
            log.error("Firebase Admin SDK 초기화 실패: {}", e.getMessage());
            log.warn("FCM 기능이 비활성화됩니다. firebase-service-account.json 파일을 확인해주세요.");
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("FirebaseApp이 초기화되지 않아 FirebaseMessaging Bean을 생성할 수 없습니다.");
            return null;
        }
        return FirebaseMessaging.getInstance();
    }
}
