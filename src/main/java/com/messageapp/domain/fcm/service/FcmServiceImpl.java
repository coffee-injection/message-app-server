package com.messageapp.domain.fcm.service;

import com.google.firebase.messaging.*;
import com.messageapp.domain.fcm.dto.DeviceTokenRequest;
import com.messageapp.domain.fcm.entity.DeviceToken;
import com.messageapp.domain.fcm.repository.DeviceTokenRepository;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import com.messageapp.global.exception.business.member.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmServiceImpl implements FcmService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final MemberRepository memberRepository;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    @Transactional
    public void registerToken(Long memberId, DeviceTokenRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        String fcmToken = request.getFcmToken();

        try {
            deviceTokenRepository.findByFcmToken(fcmToken)
                    .ifPresentOrElse(
                            existingToken -> updateExistingToken(existingToken, member, request),
                            () -> saveNewToken(member, request)
                    );
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 인한 중복 발생 시, 기존 토큰 업데이트
            handleDuplicateToken(fcmToken, member, request);
        }

        log.info("FCM 토큰 등록 완료: memberId = {}", memberId);
    }

    private void updateExistingToken(DeviceToken existingToken, Member member, DeviceTokenRequest request) {
        if (!existingToken.getMember().getId().equals(member.getId())) {
            // 다른 회원의 토큰이면 소유자 변경
            existingToken.updateMember(member);
            existingToken.updateDeviceType(request.getDeviceType());
            log.info("FCM 토큰 소유자 변경: {} -> {}", existingToken.getMember().getId(), member.getId());
        }
        // 같은 회원이면 아무것도 안함 (이미 등록됨)
    }

    private void handleDuplicateToken(String fcmToken, Member member, DeviceTokenRequest request) {
        log.warn("FCM 토큰 중복 발생, 재조회 후 업데이트: fcmToken = {}", fcmToken);
        deviceTokenRepository.findByFcmToken(fcmToken)
                .ifPresent(existingToken -> updateExistingToken(existingToken, member, request));
    }

    private void saveNewToken(Member member, DeviceTokenRequest request) {
        DeviceToken deviceToken = DeviceToken.builder()
                .member(member)
                .fcmToken(request.getFcmToken())
                .deviceType(request.getDeviceType())
                .build();
        deviceTokenRepository.save(deviceToken);
    }

    @Override
    @Transactional
    public void removeToken(Long memberId, String fcmToken) {
        deviceTokenRepository.deleteByMemberIdAndFcmToken(memberId, fcmToken);
        log.info("FCM 토큰 삭제 완료: memberId = {}", memberId);
    }

    @Override
    @Async("fcmTaskExecutor")
    public void sendNotification(Long receiverId, String title, String body) {
        if (firebaseMessaging == null) {
            log.warn("FirebaseMessaging이 초기화되지 않아 푸시 알림을 발송할 수 없습니다.");
            return;
        }

        List<DeviceToken> deviceTokens = deviceTokenRepository.findByMemberId(receiverId);

        if (deviceTokens.isEmpty()) {
            log.debug("수신자의 등록된 디바이스 토큰이 없습니다: receiverId = {}", receiverId);
            return;
        }

        for (DeviceToken deviceToken : deviceTokens) {
            try {
                Message message = Message.builder()
                        .setToken(deviceToken.getFcmToken())
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .setApnsConfig(ApnsConfig.builder()
                                .setAps(Aps.builder()
                                        .setSound("default")
                                        .setBadge(1)
                                        .build())
                                .build())
                        .setAndroidConfig(AndroidConfig.builder()
                                .setNotification(AndroidNotification.builder()
                                        .setSound("default")
                                        .build())
                                .build())
                        .build();

                String response = firebaseMessaging.send(message);
                log.info("FCM 발송 성공: receiverId = {}, messageId = {}", receiverId, response);

            } catch (FirebaseMessagingException e) {
                handleFcmException(deviceToken, e);
            }
        }
    }

    @Override
    @Async("fcmTaskExecutor")
    public void sendLetterArrivalNotification(Long receiverId, String senderName) {
        String title = "새 편지가 도착했어요!";
        String body = senderName + "님이 보낸 편지가 도착했습니다.";
        sendNotification(receiverId, title, body);
    }

    private void handleFcmException(DeviceToken deviceToken, FirebaseMessagingException e) {
        MessagingErrorCode errorCode = e.getMessagingErrorCode();

        if (errorCode == MessagingErrorCode.UNREGISTERED ||
            errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
            log.warn("유효하지 않은 FCM 토큰 삭제: token = {}", deviceToken.getFcmToken());
            deviceTokenRepository.delete(deviceToken);
        } else {
            log.error("FCM 발송 실패: token = {}, error = {}",
                    deviceToken.getFcmToken(), e.getMessage());
        }
    }
}
