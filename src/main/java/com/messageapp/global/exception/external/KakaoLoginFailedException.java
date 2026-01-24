package com.messageapp.global.exception.external;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class KakaoLoginFailedException extends AppException {

    public KakaoLoginFailedException() {
        super(ErrorCode.KAKAO_LOGIN_FAILED);
    }

    public KakaoLoginFailedException(String additionalMessage) {
        super(ErrorCode.KAKAO_LOGIN_FAILED, additionalMessage);
    }
}
