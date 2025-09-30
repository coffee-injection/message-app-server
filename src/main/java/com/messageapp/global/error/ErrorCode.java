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

    /* 500 */
    INTERNAL_SERVER_ERROR(500,"서버 에러"),
    JSON_PARSE_ERROR(500,"json 파싱 에러 입니다.");

    private final int status;
    private final String reason;
}
