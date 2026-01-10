package com.messageapp.global.exception.business.letter;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class SenderNotFoundException extends AppException {

    public static final AppException EXCEPTION = new SenderNotFoundException();

    private SenderNotFoundException() {
        super(ErrorCode.SENDER_NOT_FOUND);
    }
}
