package com.messageapp.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Apple 로그인 요청 DTO
 *
 * <p>클라이언트에서 전달받은 Apple idToken을 담습니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Getter
@NoArgsConstructor
public class AppleLoginRequest {

    @NotBlank(message = "idToken은 필수입니다.")
    private String idToken;
}
