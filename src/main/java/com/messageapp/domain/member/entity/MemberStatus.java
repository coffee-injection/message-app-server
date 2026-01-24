package com.messageapp.domain.member.entity;

/**
 * 회원 상태 열거형
 *
 * <p>회원의 현재 상태를 나타냅니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see Member
 */
public enum MemberStatus {

    /** 활성 상태 - 정상적으로 서비스 이용 가능 */
    ACTIVE,

    /** 비활성 상태 - 탈퇴한 회원 (소프트 삭제) */
    INACTIVE,

    /** 차단 상태 - 관리자에 의해 차단된 회원 */
    BANNED
}
