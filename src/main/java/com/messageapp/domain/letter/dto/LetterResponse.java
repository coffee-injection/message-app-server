package com.messageapp.domain.letter.dto;

import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.entity.LetterStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LetterResponse {

    private Long letterId;
    private String content;
    private String senderName;
    private String senderIslandName;
    private Integer senderProfileImageIndex;
    private LetterStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime matchedAt;
    private LocalDateTime readAt;

    public static LetterResponse from(Letter letter) {
        return LetterResponse.builder()
                .letterId(letter.getId())
                .content(letter.getContent())
                .senderName(letter.getSender().getName())
                .senderIslandName(letter.getSender().getIslandName())
                .senderProfileImageIndex(letter.getSender().getProfileImageIndex())
                .status(letter.getStatus())
                .createdAt(letter.getCreatedAt())
                .matchedAt(letter.getMatchedAt())
                .readAt(letter.getReadAt())
                .build();
    }
}
