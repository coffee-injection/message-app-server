package com.messageapp.api.report;

import com.messageapp.domain.report.dto.ReportRequest;
import com.messageapp.domain.report.dto.ReportResponse;
import com.messageapp.domain.report.service.ReportService;
import com.messageapp.global.auth.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@RestController
public class ReportController {

    private final ReportService reportService;

    /**
     * 편지 신고
     */
    @PostMapping
    public ReportResponse reportLetter(
            @LoginMember Long memberId,
            @Valid @RequestBody ReportRequest request) {
        return reportService.reportLetter(request.getLetterId(), memberId, request.getReason());
    }
}
