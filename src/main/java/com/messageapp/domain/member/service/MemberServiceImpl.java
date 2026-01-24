package com.messageapp.domain.member.service;

import com.messageapp.domain.member.dto.CheckNicknameResponse;
import com.messageapp.domain.member.dto.UpdateProfileRequest;
import com.messageapp.domain.member.dto.UpdateProfileResponse;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import com.messageapp.global.exception.business.member.DuplicateNicknameException;
import com.messageapp.global.exception.business.member.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 서비스 구현체
 *
 * <p>회원 정보 조회 및 프로필 관리 기능을 제공합니다.</p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>닉네임 중복 체크</li>
 *   <li>프로필 정보 수정 (닉네임, 섬 이름, 프로필 이미지)</li>
 * </ul>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see MemberService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    /** 회원 저장소 */
    private final MemberRepository memberRepository;

    /**
     * {@inheritDoc}
     *
     * <p>DB에 동일한 닉네임이 존재하는지 조회하여 사용 가능 여부를 반환합니다.</p>
     */
    @Override
    public CheckNicknameResponse checkNickname(String nickname) {
        boolean exists = memberRepository.existsByName(nickname);
        boolean isAvailable = !exists;

        log.info("닉네임 중복 체크: nickname = {}, isAvailable = {}", nickname, isAvailable);

        return CheckNicknameResponse.of(isAvailable);
    }

    /**
     * {@inheritDoc}
     *
     * <h4>처리 흐름:</h4>
     * <ol>
     *   <li>회원 조회</li>
     *   <li>닉네임 변경 요청 시: 중복 체크 후 변경</li>
     *   <li>섬 이름 변경 요청 시: 바로 변경</li>
     *   <li>프로필 이미지 변경 요청 시: 바로 변경</li>
     * </ol>
     *
     * <p>각 필드는 null이 아닌 경우에만 업데이트됩니다 (부분 업데이트 지원).</p>
     */
    @Override
    @Transactional
    public UpdateProfileResponse updateProfile(Long memberId, UpdateProfileRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // 닉네임 변경: 현재와 다른 경우에만 중복 체크 수행
        if (request.getNickname() != null) {
            if (!request.getNickname().equals(member.getName())) {
                boolean exists = memberRepository.existsByName(request.getNickname());
                if (exists) {
                    throw new DuplicateNicknameException();
                }
                member.updateName(request.getNickname());
            }
        }

        // 섬 이름 변경
        if (request.getIslandName() != null) {
            member.updateIslandName(request.getIslandName());
        }

        // 프로필 이미지 인덱스 변경
        if (request.getProfileImageIndex() != null) {
            member.updateProfileImageIndex(request.getProfileImageIndex());
        }

        log.info("프로필 수정 완료: memberId = {}, nickname = {}, islandName = {}, profileImageIndex = {}",
                memberId, member.getName(), member.getIslandName(), member.getProfileImageIndex());

        return UpdateProfileResponse.from(member);
    }
}
