package com.messageapp.global.exception.business.member;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class DuplicateMemberException extends AppException {

    public DuplicateMemberException() {
        super(ErrorCode.DUPLICATE_MEMBER);
    }

    public DuplicateMemberException(String additionalMessage) {
        super(ErrorCode.DUPLICATE_MEMBER, additionalMessage);
    }
}
