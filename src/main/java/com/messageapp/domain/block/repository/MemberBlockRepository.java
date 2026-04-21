package com.messageapp.domain.block.repository;

import com.messageapp.domain.block.entity.MemberBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 회원 차단 저장소
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Repository
public interface MemberBlockRepository extends JpaRepository<MemberBlock, Long> {

    /**
     * 중복 차단 여부 확인
     */
    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    /**
     * 특정 회원이 차단한 회원 ID 목록 조회
     */
    @Query("SELECT mb.blocked.id FROM MemberBlock mb WHERE mb.blocker.id = :blockerId")
    List<Long> findBlockedIdsByBlockerId(@Param("blockerId") Long blockerId);

    /**
     * 특정 회원을 차단한 회원 ID 목록 조회
     */
    @Query("SELECT mb.blocker.id FROM MemberBlock mb WHERE mb.blocked.id = :blockedId")
    List<Long> findBlockerIdsByBlockedId(@Param("blockedId") Long blockedId);

    /**
     * 차단 해제
     */
    void deleteByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    /**
     * 특정 회원이 차단한 목록 조회
     */
    List<MemberBlock> findByBlockerId(Long blockerId);
}
