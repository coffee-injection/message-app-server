package com.messageapp.global.exception.external;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class KakaoLoginFailedException extends AppException {

    public static final AppException EXCEPTION = new KakaoLoginFailedException();

    private KakaoLoginFailedException() {
        super(ErrorCode.KAKAO_LOGIN_FAILED);
    }
}
