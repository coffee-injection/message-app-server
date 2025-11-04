package com.messageapp.domain.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequest {

    @NotNull(message = "신고할 편지 ID를 입력해주세요.")
    private Long letterId;

    @NotBlank(message = "신고 사유를 입력해주세요.")
    @Size(min = 1, max = 500, message = "신고 사유는 1자 이상 500자 이하로 입력해주세요.")
    private String reason;
}
