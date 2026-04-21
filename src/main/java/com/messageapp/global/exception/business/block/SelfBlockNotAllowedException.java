package com.messageapp.global.exception.business.block;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class SelfBlockNotAllowedException extends AppException {

    public SelfBlockNotAllowedException() {
        super(ErrorCode.SELF_BLOCK_NOT_ALLOWED);
    }

    public SelfBlockNotAllowedException(String additionalMessage) {
        super(ErrorCode.SELF_BLOCK_NOT_ALLOWED, additionalMessage);
    }
}
