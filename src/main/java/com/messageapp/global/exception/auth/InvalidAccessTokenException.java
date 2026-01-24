package com.messageapp.global.exception.auth;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class InvalidAccessTokenException extends AppException {

    public InvalidAccessTokenException() {
        super(ErrorCode.INVALID_ACCESS_TOKEN);
    }

    public InvalidAccessTokenException(String additionalMessage) {
        super(ErrorCode.INVALID_ACCESS_TOKEN, additionalMessage);
    }
}
