package com.messageapp.global.constant;

/**
 * 회원 관련 상수 정의 클래스
 *
 * <p>회원 정보의 유효성 검증에 사용되는 상수들을 정의합니다.
 * 매직 넘버 사용을 방지하고 일관된 검증 기준을 제공합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
public final class MemberConstants {

    /**
     * 인스턴스 생성 방지를 위한 private 생성자
     */
    private MemberConstants() {
    }

    // ===== 닉네임 관련 상수 =====

    /** 닉네임 최소 길이 */
    public static final int NICKNAME_MIN_LENGTH = 2;

    /** 닉네임 최대 길이 */
    public static final int NICKNAME_MAX_LENGTH = 10;

    // ===== 섬 이름 관련 상수 =====

    /** 섬 이름 최소 길이 */
    public static final int ISLAND_NAME_MIN_LENGTH = 1;

    /** 섬 이름 최대 길이 */
    public static final int ISLAND_NAME_MAX_LENGTH = 8;

    // ===== 프로필 이미지 관련 상수 =====

    /** 프로필 이미지 인덱스 최소값 */
    public static final int PROFILE_IMAGE_MIN_INDEX = 1;

    /** 프로필 이미지 인덱스 최대값 */
    public static final int PROFILE_IMAGE_MAX_INDEX = 21;
}
