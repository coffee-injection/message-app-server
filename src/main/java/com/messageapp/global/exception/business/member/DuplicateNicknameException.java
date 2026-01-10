package com.messageapp.global.exception.business.member;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class DuplicateNicknameException extends AppException {

    public static final AppException EXCEPTION = new DuplicateNicknameException();

    private DuplicateNicknameException() {
        super(ErrorCode.DUPLICATE_NICKNAME);
    }
}
