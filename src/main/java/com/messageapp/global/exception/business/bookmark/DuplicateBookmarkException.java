package com.messageapp.global.exception.business.bookmark;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class DuplicateBookmarkException extends AppException {

    public static final AppException EXCEPTION = new DuplicateBookmarkException();

    private DuplicateBookmarkException() {
        super(ErrorCode.DUPLICATE_BOOKMARK);
    }
}
