package com.messageapp.domain.letter.dto;

import com.messageapp.domain.letter.entity.Letter;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 편지 ID 응답 DTO
 *
 * <p>수신 편지 목록 조회 시 반환되는 간략한 편지 정보입니다.
 * 상세 정보는 개별 조회 API를 통해 확인합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public class LetterIdResponse {

    /** 편지 ID */
    private Long letterId;

    /**
     * Letter 엔티티로부터 LetterIdResponse를 생성합니다.
     *
     * @param letter 편지 엔티티
     * @return 편지 ID 응답 DTO
     */
    public static LetterIdResponse from(Letter letter) {
        return new LetterIdResponse(letter.getId());
    }
}
