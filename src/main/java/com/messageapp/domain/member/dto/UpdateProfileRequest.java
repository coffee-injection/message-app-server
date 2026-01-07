package com.messageapp.domain.member.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
    private String nickname;

    @Size(min = 2, max = 8, message = "섬 이름은 2자 이상 8자 이하로 입력해주세요.")
    private String islandName;

    @Min(value = 1, message = "프로필 이미지 인덱스는 1 이상이어야 합니다.")
    @Max(value = 21, message = "프로필 이미지 인덱스는 21 이하여야 합니다.")
    private Integer profileImageIndex;
}
