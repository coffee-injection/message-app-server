package com.messageapp.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 */
    NOT_NULL_TOKEN(400, "토큰 값이 NULL일 수 없습니다"),
    MISMATCH_USER_OAUTH_ID(400, "유저의  OAuth ID값이 토큰 ID 값과 일치하지 않습니다"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_TOKEN(401, "토큰이 유효하지 않습니다."),
    EXPIRED_TOKEN(401, "토큰이 만료되었습니다."),

    /* 403 UNAUTHORIZED : 인가되지 않은 사용자 */
    REFRESH_TOKEN_EXPIRED_TOKEN(HttpStatus.FORBIDDEN.value(),"refreshToken 만료."),
    NECESSARY_LOGIN(403,"로그인이 반드시 필요한 서비스입니다"),
    /* 404 NOT_FOUND : Resource를 찾을 수 없음 */
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    AlREADY_REGISTER(404, "이미 등록된 회원입니다"),

    /* 400 BAD_REQUEST - 추가: 잘못된 요청, 유효성 검증 실패, 리소스를 찾을 수 없음 */
    INVALID_ISLAND_NAME(400, "섬 이름은 2~8글자여야 합니다."),
    INVALID_PROFILE_IMAGE_INDEX(400, "프로필 이미지 인덱스는 1~21 범위여야 합니다."),
    INVALID_TEMP_TOKEN(400, "임시 토큰이 아닙니다."),
    MEMBER_NOT_FOUND(400, "회원을 찾을 수 없습니다."),
    LETTER_NOT_FOUND(400, "편지를 찾을 수 없습니다."),
    BOOKMARK_NOT_FOUND(400, "북마크를 찾을 수 없습니다."),
    SENDER_NOT_FOUND(400, "발신자를 찾을 수 없습니다."),
    REPORTER_NOT_FOUND(400, "신고자를 찾을 수 없습니다."),
    NO_AVAILABLE_RECEIVER(400, "편지를 받을 수 있는 회원이 없습니다."),

    /* 401 UNAUTHORIZED - 추가: 인증 실패 */
    INVALID_ACCESS_TOKEN(401, "유효하지 않은 액세스 토큰입니다."),

    /* 403 FORBIDDEN - 추가: 권한 없음 */
    LETTER_ACCESS_DENIED(403, "해당 편지를 조회할 권한이 없습니다."),
    BOOKMARK_ACCESS_DENIED(403, "본인에게 수신된 편지만 북마크할 수 있습니다."),

    /* 409 CONFLICT - 추가: 중복/충돌 */
    DUPLICATE_NICKNAME(409, "이미 사용 중인 닉네임입니다."),
    DUPLICATE_MEMBER(409, "이미 가입된 회원입니다."),
    DUPLICATE_BOOKMARK(409, "이미 북마크한 편지입니다."),
    DUPLICATE_REPORT(409, "이미 신고한 편지입니다."),

    /* 500 INTERNAL_SERVER_ERROR - 추가: 서버 에러 */
    KAKAO_LOGIN_FAILED(500, "카카오 로그인 처리 중 오류가 발생했습니다."),
    KAKAO_USER_INFO_FAILED(500, "카카오 사용자 정보 조회 중 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(500,"서버 에러"),
    JSON_PARSE_ERROR(500,"json 파싱 에러 입니다.");

    private final int status;
    private final String reason;
}
