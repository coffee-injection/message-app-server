package com.messageapp.global.exception.external;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class GoogleUserInfoFailedException extends AppException {

    public static final AppException EXCEPTION = new GoogleUserInfoFailedException();

    private GoogleUserInfoFailedException() {
        super(ErrorCode.GOOGLE_USER_INFO_FAILED);
    }
}
