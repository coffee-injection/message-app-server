package com.messageapp.global.exception.business.letter;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class SenderNotFoundException extends AppException {

    public SenderNotFoundException() {
        super(ErrorCode.SENDER_NOT_FOUND);
    }

    public SenderNotFoundException(String additionalMessage) {
        super(ErrorCode.SENDER_NOT_FOUND, additionalMessage);
    }
}
