package com.messageapp.api.letter;

import com.messageapp.domain.letter.dto.LetterIdResponse;
import com.messageapp.domain.letter.dto.LetterRequest;
import com.messageapp.domain.letter.dto.LetterResponse;
import com.messageapp.domain.letter.service.LetterService;
import com.messageapp.global.auth.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;

@Tag(name = "편지", description = "편지 관련 API")
@SecurityRequirement(name = "JWT")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/letter")
@RestController
public class LetterController {

    private final LetterService letterService;

    @Operation(summary = "수신한 편지 목록 조회", description = "로그인한 사용자가 받은 편지 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LetterIdResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/list")
    public List<LetterIdResponse> getReceivedLetters(
            @Parameter(hidden = true) @LoginMember Long memberId) {
        return letterService.getReceivedLetters(memberId);
    }

    @Operation(summary = "편지 발송", description = "랜덤 수신자에게 편지를 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발송 성공",
                    content = @Content(schema = @Schema(implementation = LetterResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/send")
    public LetterResponse sendLetter(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Valid @RequestBody LetterRequest request) {
        return letterService.sendLetter(memberId, request.getContent());
    }

    @Operation(summary = "편지 상세 조회", description = "편지 ID로 편지 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = LetterResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "편지를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{letterId}")
    public LetterResponse getLetterDetail(
            @Parameter(description = "편지 ID", required = true) @PathVariable Long letterId,
            @Parameter(hidden = true) @LoginMember Long memberId) {
        return letterService.getLetterDetail(letterId, memberId);
    }
}
