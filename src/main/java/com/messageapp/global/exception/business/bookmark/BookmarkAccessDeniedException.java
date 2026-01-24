package com.messageapp.global.exception.business.bookmark;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class BookmarkAccessDeniedException extends AppException {

    public BookmarkAccessDeniedException() {
        super(ErrorCode.BOOKMARK_ACCESS_DENIED);
    }

    public BookmarkAccessDeniedException(String additionalMessage) {
        super(ErrorCode.BOOKMARK_ACCESS_DENIED, additionalMessage);
    }
}
