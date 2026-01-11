package com.messageapp.global.exception.external;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class GoogleLoginFailedException extends AppException {

    public static final AppException EXCEPTION = new GoogleLoginFailedException();

    private GoogleLoginFailedException() {
        super(ErrorCode.GOOGLE_LOGIN_FAILED);
    }
}
