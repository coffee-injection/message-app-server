package com.messageapp.global.exception.business.report;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class DuplicateReportException extends AppException {

    public DuplicateReportException() {
        super(ErrorCode.DUPLICATE_REPORT);
    }

    public DuplicateReportException(String additionalMessage) {
        super(ErrorCode.DUPLICATE_REPORT, additionalMessage);
    }
}
