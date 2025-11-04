package com.messageapp.domain.letter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LetterRequest {

    @NotBlank(message = "편지 내용을 입력해주세요.")
    @Size(min = 1, max = 1000, message = "편지 내용은 1자 이상 1000자 이하로 입력해주세요.")
    private String content;
}
