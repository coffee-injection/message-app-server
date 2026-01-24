package com.messageapp.global.exception.validation;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class InvalidTempTokenException extends AppException {

    public InvalidTempTokenException() {
        super(ErrorCode.INVALID_TEMP_TOKEN);
    }

    public InvalidTempTokenException(String additionalMessage) {
        super(ErrorCode.INVALID_TEMP_TOKEN, additionalMessage);
    }
}
