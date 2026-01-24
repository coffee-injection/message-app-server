package com.messageapp.domain.member.dto;

import com.messageapp.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 프로필 수정 응답 DTO
 *
 * <p>프로필 수정 완료 후 반환되는 업데이트된 회원 정보입니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public class UpdateProfileResponse {

    /** 회원 ID */
    private Long memberId;

    /** 수정된 닉네임 */
    private String nickname;

    /** 수정된 섬 이름 */
    private String islandName;

    /** 수정된 프로필 이미지 인덱스 */
    private Integer profileImageIndex;

    /**
     * Member 엔티티로부터 UpdateProfileResponse를 생성합니다.
     *
     * @param member 회원 엔티티
     * @return 프로필 수정 응답 DTO
     */
    public static UpdateProfileResponse from(Member member) {
        return new UpdateProfileResponse(
            member.getId(),
            member.getName(),
            member.getIslandName(),
            member.getProfileImageIndex()
        );
    }
}
