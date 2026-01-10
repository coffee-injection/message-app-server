package com.messageapp.global.exception.business.member;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class DuplicateMemberException extends AppException {

    public static final AppException EXCEPTION = new DuplicateMemberException();

    private DuplicateMemberException() {
        super(ErrorCode.DUPLICATE_MEMBER);
    }
}
