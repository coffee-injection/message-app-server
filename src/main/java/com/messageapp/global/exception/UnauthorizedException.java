package com.messageapp.global.exception;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class UnauthorizedException extends AppException {

    public static final AppException EXCEPTION = new UnauthorizedException();

    private UnauthorizedException() {
        super(ErrorCode.NECESSARY_LOGIN);
    }

}
