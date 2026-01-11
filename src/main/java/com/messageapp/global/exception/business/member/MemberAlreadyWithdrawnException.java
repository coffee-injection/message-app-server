package com.messageapp.global.exception.business.member;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class MemberAlreadyWithdrawnException extends AppException {

    public static final AppException EXCEPTION = new MemberAlreadyWithdrawnException();

    private MemberAlreadyWithdrawnException() {
        super(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
    }
}
