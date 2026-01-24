package com.messageapp.domain.letter.entity;

/**
 * 편지 상태 열거형
 *
 * <p>편지의 현재 상태를 나타냅니다.</p>
 *
 * <h3>상태 전이:</h3>
 * <pre>
 * WAITING → DELIVERED → READ
 * (생성)   (수신자 배정) (읽음)
 * </pre>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see Letter
 */
public enum LetterStatus {

    /** 대기 중 - 수신자 배정 전 */
    WAITING,

    /** 배달 완료 - 수신자에게 배정됨, 아직 읽지 않음 */
    DELIVERED,

    /** 읽음 - 수신자가 편지를 열람함 */
    READ
}
