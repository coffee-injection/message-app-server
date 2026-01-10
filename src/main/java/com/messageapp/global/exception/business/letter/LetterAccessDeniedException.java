package com.messageapp.global.exception.business.letter;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class LetterAccessDeniedException extends AppException {

    public static final AppException EXCEPTION = new LetterAccessDeniedException();

    private LetterAccessDeniedException() {
        super(ErrorCode.LETTER_ACCESS_DENIED);
    }
}
