package com.messageapp.api.member;

import com.messageapp.domain.member.dto.CheckNicknameRequest;
import com.messageapp.domain.member.dto.CheckNicknameResponse;
import com.messageapp.domain.member.dto.UpdateProfileRequest;
import com.messageapp.domain.member.dto.UpdateProfileResponse;
import com.messageapp.domain.member.service.MemberService;
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

@Tag(name = "회원정보", description = "회원정보 관련 API")
@SecurityRequirement(name = "JWT")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@RestController
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "닉네임 중복 체크", description = "닉네임을 중복체크 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "중복 체크 성공",
                    content = @Content(schema = @Schema(implementation = CheckNicknameResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    })
    @PostMapping("/check-nickname")
    public CheckNicknameResponse checkNickname(
            @Valid @RequestBody CheckNicknameRequest request) {
        return memberService.checkNickname(request.getNickname());
    }

    @Operation(summary = "프로필 정보 수정", description = "회원의 닉네임, 섬 이름, 프로필 이미지를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공",
                    content = @Content(schema = @Schema(implementation = UpdateProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음", content = @Content)
    })
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/profile")
    public UpdateProfileResponse updateProfile(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return memberService.updateProfile(memberId, request);
    }
}
