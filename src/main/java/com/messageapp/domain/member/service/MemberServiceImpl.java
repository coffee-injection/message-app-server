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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public CheckNicknameResponse checkNickname(String nickname) {
        boolean exists = memberRepository.existsByName(nickname);
        boolean isAvailable = !exists;

        log.info("닉네임 중복 체크: nickname = {}, isAvailable = {}", nickname, isAvailable);

        return CheckNicknameResponse.of(isAvailable);
    }

    @Override
    @Transactional
    public UpdateProfileResponse updateProfile(Long memberId, UpdateProfileRequest request) {
        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.EXCEPTION);

        // 2. 닉네임 변경
        if (request.getNickname() != null) {
            // 현재 닉네임과 다른 경우에만 중복 체크
            if (!request.getNickname().equals(member.getName())) {
                boolean exists = memberRepository.existsByName(request.getNickname());
                if (exists) {
                    throw DuplicateNicknameException.EXCEPTION;
                }
                member.updateName(request.getNickname());
            }
        }

        // 3. 섬 이름 변경
        if (request.getIslandName() != null) {
            member.updateIslandName(request.getIslandName());
        }

        // 4. 프로필 이미지 인덱스 변경
        if (request.getProfileImageIndex() != null) {
            member.updateProfileImageIndex(request.getProfileImageIndex());
        }

        log.info("프로필 수정 완료: memberId = {}, nickname = {}, islandName = {}, profileImageIndex = {}",
                memberId, member.getName(), member.getIslandName(), member.getProfileImageIndex());

        return UpdateProfileResponse.from(member);
    }
}
