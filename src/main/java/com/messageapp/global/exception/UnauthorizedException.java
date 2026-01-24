package com.messageapp.global.exception;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class UnauthorizedException extends AppException {

    public UnauthorizedException() {
        super(ErrorCode.NECESSARY_LOGIN);
    }

    public UnauthorizedException(String additionalMessage) {
        super(ErrorCode.NECESSARY_LOGIN, additionalMessage);
    }
}
