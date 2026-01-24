package com.messageapp.global.exception.business.letter;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class NoAvailableReceiverException extends AppException {

    public NoAvailableReceiverException() {
        super(ErrorCode.NO_AVAILABLE_RECEIVER);
    }

    public NoAvailableReceiverException(String additionalMessage) {
        super(ErrorCode.NO_AVAILABLE_RECEIVER, additionalMessage);
    }
}
