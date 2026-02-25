package com.messageapp.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 에러 코드 열거형
 *
 * <p>API 응답에서 사용되는 에러 코드를 정의합니다.
 * 각 에러 코드는 HTTP 상태 코드와 에러 메시지를 포함합니다.</p>
 *
 * <h3>HTTP 상태 코드 분류:</h3>
 * <ul>
 *   <li>400 BAD_REQUEST: 잘못된 요청, 유효성 검증 실패</li>
 *   <li>401 UNAUTHORIZED: 인증 실패</li>
 *   <li>403 FORBIDDEN: 권한 없음</li>
 *   <li>404 NOT_FOUND: 리소스를 찾을 수 없음</li>
 *   <li>409 CONFLICT: 중복/충돌</li>
 *   <li>500 INTERNAL_SERVER_ERROR: 서버 내부 오류</li>
 * </ul>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see AppException
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ==================== 400 BAD_REQUEST ====================

    /** 토큰 값이 NULL */
    NOT_NULL_TOKEN(400, "토큰 값이 NULL일 수 없습니다"),

    /** OAuth ID 불일치 */
    MISMATCH_USER_OAUTH_ID(400, "유저의  OAuth ID값이 토큰 ID 값과 일치하지 않습니다"),

    /** 지원하지 않는 OAuth 제공자 */
    UNSUPPORTED_OAUTH_PROVIDER(400, "지원하지 않는 소셜 로그인 제공자입니다."),

    /** 유효하지 않은 섬 이름 */
    INVALID_ISLAND_NAME(400, "섬 이름은 2~8글자여야 합니다."),

    /** 유효하지 않은 프로필 이미지 인덱스 */
    INVALID_PROFILE_IMAGE_INDEX(400, "프로필 이미지 인덱스는 1~21 범위여야 합니다."),

    /** 임시 토큰이 아님 */
    INVALID_TEMP_TOKEN(400, "임시 토큰이 아닙니다."),

    /** 회원을 찾을 수 없음 */
    MEMBER_NOT_FOUND(400, "회원을 찾을 수 없습니다."),

    /** 편지를 찾을 수 없음 */
    LETTER_NOT_FOUND(400, "편지를 찾을 수 없습니다."),

    /** 북마크를 찾을 수 없음 */
    BOOKMARK_NOT_FOUND(400, "북마크를 찾을 수 없습니다."),

    /** 발신자를 찾을 수 없음 */
    SENDER_NOT_FOUND(400, "발신자를 찾을 수 없습니다."),

    /** 신고자를 찾을 수 없음 */
    REPORTER_NOT_FOUND(400, "신고자를 찾을 수 없습니다."),

    /** 수신 가능한 회원이 없음 */
    NO_AVAILABLE_RECEIVER(400, "편지를 받을 수 있는 회원이 없습니다."),

    /** 이미 탈퇴한 회원 */
    MEMBER_ALREADY_WITHDRAWN(400, "이미 탈퇴한 회원입니다."),

    /** 디바이스 토큰을 찾을 수 없음 */
    DEVICE_TOKEN_NOT_FOUND(400, "등록된 디바이스 토큰을 찾을 수 없습니다."),

    // ==================== 401 UNAUTHORIZED ====================

    /** 유효하지 않은 토큰 */
    INVALID_TOKEN(401, "토큰이 유효하지 않습니다."),

    /** 만료된 토큰 */
    EXPIRED_TOKEN(401, "토큰이 만료되었습니다."),

    /** 유효하지 않은 액세스 토큰 */
    INVALID_ACCESS_TOKEN(401, "유효하지 않은 액세스 토큰입니다."),

    /** 유효하지 않은 리프레시 토큰 */
    INVALID_REFRESH_TOKEN(401, "유효하지 않은 리프레시 토큰입니다."),

    /** 만료된 리프레시 토큰 */
    REFRESH_TOKEN_EXPIRED(401, "만료된 리프레시 토큰입니다."),

    // ==================== 403 FORBIDDEN ====================

    /** 리프레시 토큰 만료 (레거시) */
    REFRESH_TOKEN_EXPIRED_TOKEN(HttpStatus.FORBIDDEN.value(),"refreshToken 만료."),

    /** 로그인 필요 */
    NECESSARY_LOGIN(403,"로그인이 반드시 필요한 서비스입니다"),

    /** 비활성화된 회원 */
    MEMBER_NOT_ACTIVE(403, "비활성화된 회원입니다."),

    /** 편지 조회 권한 없음 */
    LETTER_ACCESS_DENIED(403, "해당 편지를 조회할 권한이 없습니다."),

    /** 북마크 권한 없음 */
    BOOKMARK_ACCESS_DENIED(403, "본인에게 수신된 편지만 북마크할 수 있습니다."),

    // ==================== 404 NOT_FOUND ====================

    /** 사용자를 찾을 수 없음 */
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),

    /** 이미 등록된 회원 */
    AlREADY_REGISTER(404, "이미 등록된 회원입니다"),

    // ==================== 409 CONFLICT ====================

    /** 닉네임 중복 */
    DUPLICATE_NICKNAME(409, "이미 사용 중인 닉네임입니다."),

    /** 회원 중복 */
    DUPLICATE_MEMBER(409, "이미 가입된 회원입니다."),

    /** 북마크 중복 */
    DUPLICATE_BOOKMARK(409, "이미 북마크한 편지입니다."),

    /** 신고 중복 */
    DUPLICATE_REPORT(409, "이미 신고한 편지입니다."),

    // ==================== 500 INTERNAL_SERVER_ERROR ====================

    /** 카카오 로그인 실패 */
    KAKAO_LOGIN_FAILED(500, "카카오 로그인 처리 중 오류가 발생했습니다."),

    /** 카카오 사용자 정보 조회 실패 */
    KAKAO_USER_INFO_FAILED(500, "카카오 사용자 정보 조회 중 오류가 발생했습니다."),

    /** 카카오 연결 해제 실패 */
    KAKAO_UNLINK_FAILED(500, "카카오 연결 끊기 처리 중 오류가 발생했습니다."),

    /** 구글 로그인 실패 */
    GOOGLE_LOGIN_FAILED(500, "구글 로그인 처리 중 오류가 발생했습니다."),

    /** 구글 사용자 정보 조회 실패 */
    GOOGLE_USER_INFO_FAILED(500, "구글 사용자 정보 조회 중 오류가 발생했습니다."),

    /** 애플 로그인 실패 */
    APPLE_LOGIN_FAILED(500, "애플 로그인 처리 중 오류가 발생했습니다."),

    /** 서버 내부 오류 */
    INTERNAL_SERVER_ERROR(500,"서버 에러"),

    /** JSON 파싱 오류 */
    JSON_PARSE_ERROR(500,"json 파싱 에러 입니다."),

    /** FCM 메시지 발송 실패 */
    FCM_SEND_FAILED(500, "FCM 메시지 발송에 실패했습니다."),

    /** FCM 초기화 실패 */
    FCM_INITIALIZATION_FAILED(500, "FCM 초기화에 실패했습니다.");

    /** HTTP 상태 코드 */
    private final int status;

    /** 에러 메시지 */
    private final String reason;
}
