package com.messageapp.domain.block.dto;

import com.messageapp.domain.block.entity.MemberBlock;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 차단 응답 DTO
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "차단 응답")
public class BlockResponse {

    @Schema(description = "차단 ID", example = "1")
    private Long blockId;

    @Schema(description = "차단된 회원 ID", example = "5")
    private Long blockedMemberId;

    @Schema(description = "차단된 회원 닉네임", example = "익명의섬주민")
    private String blockedMemberName;

    @Schema(description = "차단 시각")
    private LocalDateTime blockedAt;

    public static BlockResponse from(MemberBlock block) {
        return BlockResponse.builder()
                .blockId(block.getId())
                .blockedMemberId(block.getBlocked().getId())
                .blockedMemberName(block.getBlocked().getName())
                .blockedAt(block.getCreatedAt())
                .build();
    }
}
