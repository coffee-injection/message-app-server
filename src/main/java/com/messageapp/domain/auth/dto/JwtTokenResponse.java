package com.messageapp.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenResponse {

    private String accessToken;
    private String tokenType;
    private Long expiresIn; // 초 단위
    private Long memberId;
    private String email;
    private Boolean isNewMember; // 최초 가입 여부
}
