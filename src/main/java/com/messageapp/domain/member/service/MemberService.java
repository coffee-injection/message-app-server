package com.messageapp.domain.member.service;

import com.messageapp.domain.member.dto.CheckNicknameResponse;
import com.messageapp.domain.member.dto.UpdateProfileRequest;
import com.messageapp.domain.member.dto.UpdateProfileResponse;

public interface MemberService {

    /**
     * 닉네임 중복 체크
     */
    CheckNicknameResponse checkNickname(String nickname);

    /**
     * 프로필 정보 수정
     */
    UpdateProfileResponse updateProfile(Long memberId, UpdateProfileRequest request);
}
