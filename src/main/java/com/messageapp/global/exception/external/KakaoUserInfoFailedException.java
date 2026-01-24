package com.messageapp.global.exception.external;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class KakaoUserInfoFailedException extends AppException {

    public KakaoUserInfoFailedException() {
        super(ErrorCode.KAKAO_USER_INFO_FAILED);
    }

    public KakaoUserInfoFailedException(String additionalMessage) {
        super(ErrorCode.KAKAO_USER_INFO_FAILED, additionalMessage);
    }
}
