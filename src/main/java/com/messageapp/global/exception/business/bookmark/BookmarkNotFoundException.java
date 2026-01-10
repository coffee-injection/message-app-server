package com.messageapp.global.exception.business.bookmark;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class BookmarkNotFoundException extends AppException {

    public static final AppException EXCEPTION = new BookmarkNotFoundException();

    private BookmarkNotFoundException() {
        super(ErrorCode.BOOKMARK_NOT_FOUND);
    }
}
