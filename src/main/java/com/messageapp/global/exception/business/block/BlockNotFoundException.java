package com.messageapp.global.exception.business.block;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class BlockNotFoundException extends AppException {

    public BlockNotFoundException() {
        super(ErrorCode.BLOCK_NOT_FOUND);
    }

    public BlockNotFoundException(String additionalMessage) {
        super(ErrorCode.BLOCK_NOT_FOUND, additionalMessage);
    }
}
