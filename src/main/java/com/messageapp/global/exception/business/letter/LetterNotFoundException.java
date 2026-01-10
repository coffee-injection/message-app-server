package com.messageapp.global.exception.business.letter;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class LetterNotFoundException extends AppException {

    public static final AppException EXCEPTION = new LetterNotFoundException();

    private LetterNotFoundException() {
        super(ErrorCode.LETTER_NOT_FOUND);
    }
}
