package com.messageapp.api.block;

import com.messageapp.domain.block.dto.BlockRequest;
import com.messageapp.domain.block.dto.BlockResponse;
import com.messageapp.domain.block.service.MemberBlockService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 회원 차단 컨트롤러
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Tag(name = "차단", description = "회원 차단 관련 API")
@SecurityRequirement(name = "JWT")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/block")
@RestController
public class BlockController {

    private final MemberBlockService memberBlockService;

    @Operation(summary = "발신자 차단", description = "편지의 발신자를 차단합니다. 차단 후 해당 발신자의 편지를 받지 않습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차단 성공",
                    content = @Content(schema = @Schema(implementation = BlockResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "편지를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 차단한 회원", content = @Content)
    })
    @PostMapping
    public BlockResponse blockSender(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Valid @RequestBody BlockRequest request) {
        return memberBlockService.blockSender(request.getLetterId(), memberId);
    }

    @Operation(summary = "차단 해제", description = "차단한 회원을 해제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "차단 해제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "차단 정보를 찾을 수 없음", content = @Content)
    })
    @DeleteMapping("/{blockedId}")
    public ResponseEntity<Void> unblock(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Parameter(description = "차단 해제할 회원 ID") @PathVariable Long blockedId) {
        memberBlockService.unblock(blockedId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "차단 목록 조회", description = "내가 차단한 회원 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping
    public List<BlockResponse> getBlockList(
            @Parameter(hidden = true) @LoginMember Long memberId) {
        return memberBlockService.getBlockList(memberId);
    }
}
