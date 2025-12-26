package com.messageapp.api.report;

import com.messageapp.domain.report.dto.ReportRequest;
import com.messageapp.domain.report.dto.ReportResponse;
import com.messageapp.domain.report.service.ReportService;
import com.messageapp.global.auth.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "신고", description = "신고 관련 API")
@SecurityRequirement(name = "JWT")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@RestController
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "편지 신고", description = "부적절한 편지를 신고합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고 성공",
                    content = @Content(schema = @Schema(implementation = ReportResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "편지를 찾을 수 없음", content = @Content)
    })
    @PostMapping
    public ReportResponse reportLetter(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Valid @RequestBody ReportRequest request) {
        return reportService.reportLetter(request.getLetterId(), memberId, request.getReason());
    }
}
