package com.messageapp.domain.auth.repository;

import com.messageapp.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Refresh Token 저장소
 *
 * <p>Refresh Token 엔티티에 대한 데이터 접근을 제공합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see RefreshToken
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 토큰 값으로 Refresh Token을 조회합니다.
     *
     * @param token 조회할 토큰 값
     * @return Refresh Token Optional
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 회원 ID로 Refresh Token을 조회합니다.
     *
     * @param memberId 조회할 회원 ID
     * @return Refresh Token Optional
     */
    Optional<RefreshToken> findByMemberId(Long memberId);

    /**
     * 회원 ID로 Refresh Token을 삭제합니다.
     *
     * @param memberId 삭제할 회원 ID
     */
    void deleteByMemberId(Long memberId);
}
