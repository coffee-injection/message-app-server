package com.messageapp.global.exception.external;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class KakaoUnlinkFailedException extends AppException {

    public static final AppException EXCEPTION = new KakaoUnlinkFailedException();

    private KakaoUnlinkFailedException() {
        super(ErrorCode.KAKAO_UNLINK_FAILED);
    }
}
