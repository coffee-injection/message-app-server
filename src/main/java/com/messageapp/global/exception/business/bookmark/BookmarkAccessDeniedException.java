package com.messageapp.global.exception.business.bookmark;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class BookmarkAccessDeniedException extends AppException {

    public static final AppException EXCEPTION = new BookmarkAccessDeniedException();

    private BookmarkAccessDeniedException() {
        super(ErrorCode.BOOKMARK_ACCESS_DENIED);
    }
}
