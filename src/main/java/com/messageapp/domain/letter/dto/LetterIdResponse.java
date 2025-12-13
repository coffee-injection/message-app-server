package com.messageapp.domain.letter.dto;

import com.messageapp.domain.letter.entity.Letter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LetterIdResponse {

    private Long letterId;

    public static LetterIdResponse from(Letter letter) {
        return new LetterIdResponse(letter.getId());
    }
}
