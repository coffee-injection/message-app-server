package com.messageapp.api.fcm;

import com.messageapp.domain.fcm.dto.DeviceTokenRequest;
import com.messageapp.domain.fcm.service.FcmService;
import com.messageapp.global.auth.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "FCM 토큰", description = "FCM 디바이스 토큰 관리 API")
@SecurityRequirement(name = "JWT")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
@RestController
public class FcmController {

    private final FcmService fcmService;

    @Operation(summary = "FCM 토큰 등록", description = "클라이언트의 FCM 디바이스 토큰을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 등록 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/token")
    public ResponseEntity<Void> registerToken(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @Valid @RequestBody DeviceTokenRequest request) {
        fcmService.registerToken(memberId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "FCM 토큰 삭제", description = "로그아웃 시 FCM 디바이스 토큰을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @DeleteMapping("/token")
    public ResponseEntity<Void> removeToken(
            @Parameter(hidden = true) @LoginMember Long memberId,
            @RequestParam String fcmToken) {
        fcmService.removeToken(memberId, fcmToken);
        return ResponseEntity.ok().build();
    }
}
