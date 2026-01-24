package com.messageapp.domain.member.repository;

import com.messageapp.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 회원 저장소
 *
 * <p>회원 엔티티에 대한 데이터 접근을 제공합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see Member
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 이메일로 회원을 조회합니다.
     *
     * @param email 조회할 이메일
     * @return 회원 Optional
     */
    Optional<Member> findByEmail(String email);

    /**
     * 발신자를 제외한 활성 회원 목록을 조회합니다.
     *
     * @param senderId 제외할 발신자 ID
     * @return 활성 회원 목록
     * @deprecated 성능 이슈로 인해 {@link #findRandomActiveMember(Long)} 사용 권장.
     *             전체 회원을 메모리에 로드하므로 대용량 데이터에서 비효율적.
     */
    @Deprecated
    @Query("SELECT m FROM Member m WHERE m.id != :senderId AND m.status = 'ACTIVE'")
    List<Member> findActiveMembers(@Param("senderId") Long senderId);

    /**
     * 발신자를 제외한 활성 회원 중 랜덤으로 1명을 선택합니다.
     *
     * <p>DB에서 직접 랜덤 선택(ORDER BY RAND() LIMIT 1)을 수행하여
     * 메모리 사용을 최소화합니다.</p>
     *
     * <p><b>Note:</b> MySQL의 RAND() 함수를 사용하므로 MySQL 환경에서만 동작합니다.</p>
     *
     * @param senderId 제외할 발신자 ID
     * @return 랜덤 선택된 활성 회원 Optional
     */
    @Query(value = "SELECT * FROM members WHERE member_id != :senderId AND status = 'ACTIVE' ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Member> findRandomActiveMember(@Param("senderId") Long senderId);

    /**
     * 해당 닉네임이 이미 사용 중인지 확인합니다.
     *
     * @param name 확인할 닉네임
     * @return 이미 존재하면 true
     */
    boolean existsByName(String name);
}
