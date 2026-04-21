package com.messageapp.domain.block.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 차단 요청 DTO
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "차단 요청")
public class BlockRequest {

    @NotNull(message = "편지 ID는 필수입니다.")
    @Schema(description = "차단할 발신자의 편지 ID", example = "1")
    private Long letterId;
}
