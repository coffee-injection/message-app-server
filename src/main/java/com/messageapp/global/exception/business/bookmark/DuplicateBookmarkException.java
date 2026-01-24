package com.messageapp.global.exception.business.bookmark;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class DuplicateBookmarkException extends AppException {

    public DuplicateBookmarkException() {
        super(ErrorCode.DUPLICATE_BOOKMARK);
    }

    public DuplicateBookmarkException(String additionalMessage) {
        super(ErrorCode.DUPLICATE_BOOKMARK, additionalMessage);
    }
}
