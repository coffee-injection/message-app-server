package com.messageapp.global.exception.business.letter;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class NoAvailableReceiverException extends AppException {

    public static final AppException EXCEPTION = new NoAvailableReceiverException();

    private NoAvailableReceiverException() {
        super(ErrorCode.NO_AVAILABLE_RECEIVER);
    }
}
