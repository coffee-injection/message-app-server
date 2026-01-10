package com.messageapp.global.exception.validation;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class InvalidProfileImageIndexException extends AppException {

    public static final AppException EXCEPTION = new InvalidProfileImageIndexException();

    private InvalidProfileImageIndexException() {
        super(ErrorCode.INVALID_PROFILE_IMAGE_INDEX);
    }
}
