package com.messageapp.global.exception.business.bookmark;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class BookmarkNotFoundException extends AppException {

    public BookmarkNotFoundException() {
        super(ErrorCode.BOOKMARK_NOT_FOUND);
    }

    public BookmarkNotFoundException(String additionalMessage) {
        super(ErrorCode.BOOKMARK_NOT_FOUND, additionalMessage);
    }
}
