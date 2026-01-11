package com.messageapp.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleLoginUrlResponse {
    private String loginUrl;
}
