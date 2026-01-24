package com.messageapp.global.exception.business.letter;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class LetterAccessDeniedException extends AppException {

    public LetterAccessDeniedException() {
        super(ErrorCode.LETTER_ACCESS_DENIED);
    }

    public LetterAccessDeniedException(String additionalMessage) {
        super(ErrorCode.LETTER_ACCESS_DENIED, additionalMessage);
    }
}
