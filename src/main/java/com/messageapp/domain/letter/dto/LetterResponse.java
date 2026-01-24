package com.messageapp.domain.letter.dto;

import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.entity.LetterStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 편지 응답 DTO
 *
 * <p>편지 발송 및 상세 조회 시 반환되는 편지 정보입니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LetterResponse {

    /** 편지 ID */
    private Long letterId;

    /** 편지 내용 */
    private String content;

    /** 발신자 닉네임 */
    private String senderName;

    /** 발신자 섬 이름 */
    private String senderIslandName;

    /** 발신자 프로필 이미지 인덱스 */
    private Integer senderProfileImageIndex;

    /** 편지 상태 (WAITING, DELIVERED, READ) */
    private LetterStatus status;

    /** 편지 작성 시각 */
    private LocalDateTime createdAt;

    /** 수신자 배정 시각 */
    private LocalDateTime matchedAt;

    /** 읽음 처리 시각 */
    private LocalDateTime readAt;

    /**
     * Letter 엔티티로부터 LetterResponse를 생성합니다.
     *
     * @param letter 편지 엔티티
     * @return 편지 응답 DTO
     */
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
