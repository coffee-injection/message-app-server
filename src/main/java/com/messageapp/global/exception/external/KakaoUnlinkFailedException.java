package com.messageapp.global.exception.external;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class KakaoUnlinkFailedException extends AppException {

    public KakaoUnlinkFailedException() {
        super(ErrorCode.KAKAO_UNLINK_FAILED);
    }

    public KakaoUnlinkFailedException(String additionalMessage) {
        super(ErrorCode.KAKAO_UNLINK_FAILED, additionalMessage);
    }
}
