package com.messageapp.global.exception.external;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

/**
 * Apple 로그인 실패 예외
 *
 * <p>Apple idToken 검증 실패 또는 사용자 정보 추출 실패 시 발생합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
public class AppleLoginFailedException extends AppException {

    public AppleLoginFailedException() {
        super(ErrorCode.APPLE_LOGIN_FAILED);
    }

    public AppleLoginFailedException(String additionalMessage) {
        super(ErrorCode.APPLE_LOGIN_FAILED, additionalMessage);
    }
}
