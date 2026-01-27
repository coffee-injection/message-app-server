package com.messageapp.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토큰 갱신 요청 DTO
 *
 * <p>Access Token 갱신을 위한 Refresh Token을 담는 요청 객체입니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {

    /** Refresh Token */
    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
}
