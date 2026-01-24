package com.messageapp.global.exception.business.report;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class ReporterNotFoundException extends AppException {

    public ReporterNotFoundException() {
        super(ErrorCode.REPORTER_NOT_FOUND);
    }

    public ReporterNotFoundException(String additionalMessage) {
        super(ErrorCode.REPORTER_NOT_FOUND, additionalMessage);
    }
}
