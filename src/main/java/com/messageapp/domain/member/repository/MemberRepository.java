package com.messageapp.domain.member.repository;

import com.messageapp.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    /**
     * 발신자를 제외한 활성 회원 목록 조회
     */
    @Query("SELECT m FROM Member m WHERE m.id != :senderId AND m.status = 'ACTIVE'")
    List<Member> findActiveMembers(@Param("senderId") Long senderId);

    /**
     * 닉네임 중복 확인
     */
    boolean existsByName(String name);
}
