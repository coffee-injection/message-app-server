package com.messageapp.global.error;

import lombok.Getter;

/**
 * 애플리케이션 커스텀 예외 베이스 클래스
 *
 * <p>모든 비즈니스 예외의 부모 클래스입니다.
 * {@link ErrorCode}를 통해 일관된 에러 응답을 제공합니다.</p>
 *
 * <h3>사용 예시:</h3>
 * <pre>{@code
 * // 기본 사용
 * throw new MemberNotFoundException();
 *
 * // 추가 메시지와 함께 사용
 * throw new MemberNotFoundException("memberId: " + memberId);
 * }</pre>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see ErrorCode
 */
@Getter
public class AppException extends RuntimeException {

    /** 에러 코드 (HTTP 상태 코드, 메시지 포함) */
    private final ErrorCode errorCode;

    /** 추가 상세 메시지 (디버깅용) */
    private final String additionalMessage;

    /**
     * ErrorCode만으로 예외를 생성합니다.
     *
     * @param errorCode 에러 코드
     */
    public AppException(ErrorCode errorCode) {
        super(errorCode.getReason());
        this.errorCode = errorCode;
        this.additionalMessage = null;
    }

    /**
     * ErrorCode와 추가 메시지로 예외를 생성합니다.
     *
     * <p>추가 메시지는 로깅 및 디버깅에 유용합니다.</p>
     *
     * @param errorCode 에러 코드
     * @param additionalMessage 추가 상세 메시지
     */
    public AppException(ErrorCode errorCode, String additionalMessage) {
        super(errorCode.getReason() + (additionalMessage != null ? " - " + additionalMessage : ""));
        this.errorCode = errorCode;
        this.additionalMessage = additionalMessage;
    }

    /**
     * 전체 에러 메시지를 반환합니다.
     *
     * <p>기본 메시지와 추가 메시지를 결합하여 반환합니다.</p>
     *
     * @return 전체 에러 메시지
     */
    public String getFullMessage() {
        if (additionalMessage != null && !additionalMessage.isEmpty()) {
            return errorCode.getReason() + " - " + additionalMessage;
        }
        return errorCode.getReason();
    }
}
