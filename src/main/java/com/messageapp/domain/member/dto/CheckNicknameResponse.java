package com.messageapp.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckNicknameResponse {

    private boolean isAvailable;
    private String message;

    public static CheckNicknameResponse of(boolean isAvailable) {
        String message = isAvailable
            ? "사용 가능한 닉네임입니다."
            : "이미 사용 중인 닉네임입니다.";
        return new CheckNicknameResponse(isAvailable, message);
    }
}
