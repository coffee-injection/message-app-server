package com.messageapp.domain.block.service;

import com.messageapp.domain.block.dto.BlockResponse;
import com.messageapp.domain.block.entity.MemberBlock;
import com.messageapp.domain.block.repository.MemberBlockRepository;
import com.messageapp.domain.letter.entity.Letter;
import com.messageapp.domain.letter.repository.LetterRepository;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import com.messageapp.global.exception.business.block.BlockNotFoundException;
import com.messageapp.global.exception.business.block.DuplicateBlockException;
import com.messageapp.global.exception.business.block.SelfBlockNotAllowedException;
import com.messageapp.global.exception.business.letter.LetterNotFoundException;
import com.messageapp.global.exception.business.member.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 회원 차단 서비스 구현체
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberBlockServiceImpl implements MemberBlockService {

    private final MemberBlockRepository memberBlockRepository;
    private final LetterRepository letterRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public BlockResponse blockSender(Long letterId, Long blockerId) {
        // 1. 편지 조회
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(LetterNotFoundException::new);

        // 2. 차단하는 회원 조회
        Member blocker = memberRepository.findById(blockerId)
                .orElseThrow(MemberNotFoundException::new);

        // 3. 차단 대상 (발신자)
        Member blocked = letter.getSender();

        // 4. 자기 자신 차단 방지
        if (blocker.getId().equals(blocked.getId())) {
            throw new SelfBlockNotAllowedException();
        }

        // 5. 중복 차단 체크
        if (memberBlockRepository.existsByBlockerIdAndBlockedId(blockerId, blocked.getId())) {
            throw new DuplicateBlockException();
        }

        // 6. 차단 생성 및 저장
        MemberBlock block = MemberBlock.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();

        MemberBlock savedBlock = memberBlockRepository.save(block);

        log.info("회원 차단 완료: blockerId = {}, blockedId = {}", blockerId, blocked.getId());

        return BlockResponse.from(savedBlock);
    }

    @Override
    @Transactional
    public void unblock(Long blockedId, Long blockerId) {
        // 차단 존재 여부 확인
        if (!memberBlockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            throw new BlockNotFoundException();
        }

        memberBlockRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId);

        log.info("회원 차단 해제: blockerId = {}, blockedId = {}", blockerId, blockedId);
    }

    @Override
    public List<BlockResponse> getBlockList(Long blockerId) {
        List<MemberBlock> blocks = memberBlockRepository.findByBlockerId(blockerId);

        log.info("차단 목록 조회: blockerId = {}, count = {}", blockerId, blocks.size());

        return blocks.stream()
                .map(BlockResponse::from)
                .toList();
    }
}
