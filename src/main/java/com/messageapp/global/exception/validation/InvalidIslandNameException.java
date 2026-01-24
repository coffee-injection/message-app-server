package com.messageapp.global.exception.validation;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class InvalidIslandNameException extends AppException {

    public InvalidIslandNameException() {
        super(ErrorCode.INVALID_ISLAND_NAME);
    }

    public InvalidIslandNameException(String additionalMessage) {
        super(ErrorCode.INVALID_ISLAND_NAME, additionalMessage);
    }
}
