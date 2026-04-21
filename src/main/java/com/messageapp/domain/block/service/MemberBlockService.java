package com.messageapp.domain.block.service;

import com.messageapp.domain.block.dto.BlockResponse;

import java.util.List;

/**
 * 회원 차단 서비스 인터페이스
 *
 * @author MessageApp Team
 * @since 1.0
 */
public interface MemberBlockService {

    /**
     * 편지 발신자를 차단합니다.
     *
     * @param letterId 편지 ID (발신자를 식별하기 위해)
     * @param blockerId 차단하는 회원 ID
     * @return 차단 결과
     */
    BlockResponse blockSender(Long letterId, Long blockerId);

    /**
     * 차단을 해제합니다.
     *
     * @param blockedId 차단 해제할 회원 ID
     * @param blockerId 차단한 회원 ID
     */
    void unblock(Long blockedId, Long blockerId);

    /**
     * 차단 목록을 조회합니다.
     *
     * @param blockerId 차단한 회원 ID
     * @return 차단 목록
     */
    List<BlockResponse> getBlockList(Long blockerId);
}
