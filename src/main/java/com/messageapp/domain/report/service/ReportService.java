package com.messageapp.domain.report.service;

import com.messageapp.domain.report.dto.ReportResponse;

public interface ReportService {

    /**
     * 편지 신고
     */
    ReportResponse reportLetter(Long letterId, Long reporterId, String reason);
}
