package com.messageapp.domain.member.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로필 수정 요청 DTO
 *
 * <p>회원 프로필 정보 수정 시 사용됩니다.
 * 모든 필드는 선택적이며, null이 아닌 필드만 업데이트됩니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Getter
@NoArgsConstructor
public class UpdateProfileRequest {

    /** 새 닉네임 (2~10자, 선택) */
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;

    /** 새 섬 이름 (1~8자, 선택) */
    @Size(min = 1, max = 8, message = "섬 이름은 1자 이상 8자 이하로 입력해주세요.")
    private String islandName;

    /** 새 프로필 이미지 인덱스 (1~21, 선택) */
    @Min(value = 1, message = "프로필 이미지 인덱스는 1 이상이어야 합니다.")
    @Max(value = 21, message = "프로필 이미지 인덱스는 21 이하여야 합니다.")
    private Integer profileImageIndex;
}
