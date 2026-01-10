package com.messageapp.global.exception.validation;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class InvalidIslandNameException extends AppException {

    public static final AppException EXCEPTION = new InvalidIslandNameException();

    private InvalidIslandNameException() {
        super(ErrorCode.INVALID_ISLAND_NAME);
    }
}
