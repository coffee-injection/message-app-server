package com.messageapp.global.exception.business.member;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class MemberAlreadyWithdrawnException extends AppException {

    public MemberAlreadyWithdrawnException() {
        super(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
    }

    public MemberAlreadyWithdrawnException(String additionalMessage) {
        super(ErrorCode.MEMBER_ALREADY_WITHDRAWN, additionalMessage);
    }
}
