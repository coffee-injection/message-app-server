package com.messageapp.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 완료 요청 DTO
 *
 * <p>소셜 로그인 후 회원가입을 완료하기 위한 추가 정보입니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Getter
@NoArgsConstructor
public class SignupCompleteRequest {

    /** 닉네임 (2~10자, 필수) */
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;

    /** 섬 이름 (1~8자, 선택) */
    @Size(min = 1, max = 8, message = "섬 이름은 1자 이상 8자 이하로 입력해주세요.")
    private String islandName;

    /** 프로필 이미지 인덱스 (1~21, 선택) */
    @Min(value = 1, message = "프로필 이미지 인덱스는 1 이상이어야 합니다.")
    @Max(value = 21, message = "프로필 이미지 인덱스는 21 이하여야 합니다.")
    private Integer profileImageIndex;
}
