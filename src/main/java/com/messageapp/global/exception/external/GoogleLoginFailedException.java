package com.messageapp.global.exception.external;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class GoogleLoginFailedException extends AppException {

    public GoogleLoginFailedException() {
        super(ErrorCode.GOOGLE_LOGIN_FAILED);
    }

    public GoogleLoginFailedException(String additionalMessage) {
        super(ErrorCode.GOOGLE_LOGIN_FAILED, additionalMessage);
    }
}
