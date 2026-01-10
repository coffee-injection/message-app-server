package com.messageapp.global.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.Map;

/**
 * 에러 로깅 유틸리티 클래스
 *
 * 비즈니스 예외, 시스템 예외, 검증 예외를 상세하게 로깅합니다.
 */
@Slf4j
@Component
public class ErrorLogger {

    private static final String SEPARATOR = "==================== 에러 발생 ====================";
    private static final String END_SEPARATOR = "====================================================";

    /**
     * 비즈니스 예외 로깅 (간결한 WARN 로그)
     *
     * @param e 비즈니스 예외
     * @param request HTTP 요청
     */
    public void logBusinessException(AppException e, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append(SEPARATOR).append("\n");
        sb.append("[비즈니스 예외]\n");
        sb.append("  에러 코드    : ").append(e.getErrorCode().name()).append("\n");
        sb.append("  HTTP 상태    : ").append(e.getErrorCode().getStatus()).append("\n");
        sb.append("  메시지       : ").append(e.getErrorCode().getReason()).append("\n");
        sb.append("[요청 정보]\n");
        sb.append("  요청 URL     : ").append(buildFullUrl(request)).append("\n");
        sb.append("  요청 메서드  : ").append(request.getMethod()).append("\n");
        sb.append("  원격 주소    : ").append(request.getRemoteAddr()).append("\n");
        sb.append(END_SEPARATOR);

        log.warn(sb.toString());
    }

    /**
     * 시스템 예외 로깅 (상세한 ERROR 로그)
     *
     * @param e 시스템 예외
     * @param request HTTP 요청
     */
    public void logSystemException(Exception e, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append(SEPARATOR).append("\n");
        sb.append("[시스템 예외] 예상치 못한 에러가 발생했습니다\n");
        sb.append("[요청 정보]\n");
        sb.append("  URL          : ").append(buildFullUrl(request)).append("\n");
        sb.append("  메서드       : ").append(request.getMethod()).append("\n");
        sb.append("  원격 주소    : ").append(request.getRemoteAddr()).append("\n");
        sb.append("  User-Agent   : ").append(request.getHeader("User-Agent")).append("\n");

        // 파라미터 로깅
        appendParameters(sb, request);

        // 헤더 로깅 (민감 정보 마스킹)
        appendHeaders(sb, request);

        sb.append(END_SEPARATOR);

        log.error(sb.toString());
        log.error("[스택 트레이스]", e);
    }

    /**
     * 검증 예외 로깅 (IllegalArgumentException)
     *
     * @param e 검증 예외
     * @param request HTTP 요청
     */
    public void logValidationException(IllegalArgumentException e, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append(SEPARATOR).append("\n");
        sb.append("[검증 예외]\n");
        sb.append("  메시지       : ").append(e.getMessage()).append("\n");
        sb.append("[요청 정보]\n");
        sb.append("  요청 URL     : ").append(buildFullUrl(request)).append("\n");
        sb.append("  요청 메서드  : ").append(request.getMethod()).append("\n");
        sb.append("  원격 주소    : ").append(request.getRemoteAddr()).append("\n");
        sb.append(END_SEPARATOR);

        log.warn(sb.toString());
    }

    /**
     * 전체 URL 생성 (쿼리 스트링 포함)
     */
    private String buildFullUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString != null) {
            requestURL.append('?').append(queryString);
        }

        return requestURL.toString();
    }

    /**
     * 요청 파라미터를 StringBuilder에 추가
     */
    private void appendParameters(StringBuilder sb, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (!parameterMap.isEmpty()) {
            sb.append("[파라미터]\n");
            parameterMap.forEach((key, values) ->
                    sb.append("  ").append(key).append(" = ").append(String.join(", ", values)).append("\n")
            );
        }
    }

    /**
     * 요청 헤더를 StringBuilder에 추가 (민감 정보 마스킹)
     */
    private void appendHeaders(StringBuilder sb, HttpServletRequest request) {
        sb.append("[헤더]\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);

            // 민감 정보 마스킹
            if (headerName.equalsIgnoreCase("Authorization")
                    || headerName.equalsIgnoreCase("Cookie")) {
                headerValue = maskSensitiveData(headerValue);
            }

            sb.append("  ").append(headerName).append(" = ").append(headerValue).append("\n");
        }
    }

    /**
     * 민감 정보 마스킹
     */
    private String maskSensitiveData(String data) {
        if (data == null || data.length() <= 10) {
            return "***";
        }
        return data.substring(0, 10) + "***";
    }
}
