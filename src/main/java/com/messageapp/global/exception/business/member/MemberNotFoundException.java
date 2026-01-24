package com.messageapp.global.exception.business.member;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class MemberNotFoundException extends AppException {

    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }

    public MemberNotFoundException(String additionalMessage) {
        super(ErrorCode.MEMBER_NOT_FOUND, additionalMessage);
    }

    public static MemberNotFoundException withId(Long id) {
        return new MemberNotFoundException("memberId: " + id);
    }
}
