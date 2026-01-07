package com.messageapp.api.bookmark;

import com.messageapp.domain.bookmark.dto.BookmarkRequest;
import com.messageapp.domain.bookmark.service.BookmarkService;
import com.messageapp.domain.letter.dto.LetterResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "북마크", description = "북마크 관련 API")
@SecurityRequirement(name = "JWT")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmark")
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "북마크에 편지 저장", description = "편지를 북마크에 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "편지를 찾을 수 없음", content = @Content)
    })
    @PostMapping
    public void saveLetter(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @RequestBody BookmarkRequest bookmarkRequest
    ) {
        bookmarkService.saveLetter(memberId, bookmarkRequest.getLetterId());
    }

    @Operation(summary = "북마크 편지 리스트 조회", description = "사용자가 저장한 북마크 편지 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LetterResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/list")
    public List<LetterResponse> getBookmarkList(
            @Parameter(hidden = true) @LoginMember Long memberId) {
        return bookmarkService.getBookmarkList(memberId);
    }

    @Operation(summary = "북마크 취소", description = "북마크한 편지를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "북마크를 찾을 수 없음", content = @Content)
    })
    @DeleteMapping
    public void deleteLetter(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @RequestBody BookmarkRequest bookmarkRequest
    ) {
        bookmarkService.deleteLetter(memberId, bookmarkRequest.getLetterId());
    }

}
