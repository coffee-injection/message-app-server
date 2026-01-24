package com.messageapp.global.exception.business.member;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class DuplicateNicknameException extends AppException {

    public DuplicateNicknameException() {
        super(ErrorCode.DUPLICATE_NICKNAME);
    }

    public DuplicateNicknameException(String additionalMessage) {
        super(ErrorCode.DUPLICATE_NICKNAME, additionalMessage);
    }
}
