package com.messageapp.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoLoginUrlResponse {
    private String loginUrl;
}
