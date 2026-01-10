package com.messageapp.global.exception.validation;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class InvalidTempTokenException extends AppException {

    public static final AppException EXCEPTION = new InvalidTempTokenException();

    private InvalidTempTokenException() {
        super(ErrorCode.INVALID_TEMP_TOKEN);
    }
}
