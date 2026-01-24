package com.messageapp.domain.member.service;

import com.messageapp.domain.member.dto.CheckNicknameResponse;
import com.messageapp.domain.member.dto.UpdateProfileRequest;
import com.messageapp.domain.member.dto.UpdateProfileResponse;
import com.messageapp.global.exception.business.member.DuplicateNicknameException;
import com.messageapp.global.exception.business.member.MemberNotFoundException;

/**
 * 회원 서비스 인터페이스
 *
 * <p>회원 정보 조회 및 프로필 관리 기능을 정의합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
public interface MemberService {

    /**
     * 닉네임 중복 여부를 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 사용 가능 여부를 포함한 응답
     */
    CheckNicknameResponse checkNickname(String nickname);

    /**
     * 프로필 정보를 수정합니다.
     *
     * <p>닉네임, 섬 이름, 프로필 이미지 인덱스를 부분적으로 수정할 수 있습니다.
     * null이 아닌 필드만 업데이트됩니다.</p>
     *
     * @param memberId 수정할 회원 ID
     * @param request 수정 요청 정보
     * @return 수정된 프로필 정보
     * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
     * @throws DuplicateNicknameException 닉네임이 이미 사용 중인 경우
     */
    UpdateProfileResponse updateProfile(Long memberId, UpdateProfileRequest request);
}
