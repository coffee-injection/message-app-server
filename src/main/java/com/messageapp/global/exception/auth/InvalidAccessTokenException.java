package com.messageapp.global.exception.auth;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class InvalidAccessTokenException extends AppException {

    public static final AppException EXCEPTION = new InvalidAccessTokenException();

    private InvalidAccessTokenException() {
        super(ErrorCode.INVALID_ACCESS_TOKEN);
    }
}
