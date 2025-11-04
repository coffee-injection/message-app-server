package com.messageapp.domain.report.dto;

import com.messageapp.domain.report.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private Long reportId;
    private Long letterId;
    private String reporterName;
    private String reason;
    private LocalDateTime createdAt;

    public static ReportResponse from(Report report) {
        return ReportResponse.builder()
                .reportId(report.getId())
                .letterId(report.getLetter().getId())
                .reporterName(report.getReporter().getName())
                .reason(report.getReason())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
