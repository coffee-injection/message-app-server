package com.messageapp.global.exception.business.report;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class DuplicateReportException extends AppException {

    public static final AppException EXCEPTION = new DuplicateReportException();

    private DuplicateReportException() {
        super(ErrorCode.DUPLICATE_REPORT);
    }
}
