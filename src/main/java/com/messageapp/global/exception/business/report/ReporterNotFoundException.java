package com.messageapp.global.exception.business.report;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;

public class ReporterNotFoundException extends AppException {

    public static final AppException EXCEPTION = new ReporterNotFoundException();

    private ReporterNotFoundException() {
        super(ErrorCode.REPORTER_NOT_FOUND);
    }
}
