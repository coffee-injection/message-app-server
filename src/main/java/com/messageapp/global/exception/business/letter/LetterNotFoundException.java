package com.messageapp.global.exception.business.letter;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class LetterNotFoundException extends AppException {

    public LetterNotFoundException() {
        super(ErrorCode.LETTER_NOT_FOUND);
    }

    public LetterNotFoundException(String additionalMessage) {
        super(ErrorCode.LETTER_NOT_FOUND, additionalMessage);
    }

    public static LetterNotFoundException withId(Long id) {
        return new LetterNotFoundException("letterId: " + id);
    }
}
