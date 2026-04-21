package com.messageapp.global.exception.business.block;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class DuplicateBlockException extends AppException {

    public DuplicateBlockException() {
        super(ErrorCode.DUPLICATE_BLOCK);
    }

    public DuplicateBlockException(String additionalMessage) {
        super(ErrorCode.DUPLICATE_BLOCK, additionalMessage);
    }
}
