package com.messageapp.global.slack;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Slack 에러 알림 서비스
 *
 * <p>웹훅을 통해 에러 로그를 Slack 채널로 전송합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SlackNotificationService {

    @Value("${slack.webhook.url:}")
    private String webhookUrl;

    @Value("${slack.webhook.enabled:false}")
    private boolean enabled;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    private final RestTemplate restTemplate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 시스템 예외를 Slack으로 전송합니다.
     *
     * @param e 발생한 예외
     * @param request HTTP 요청 정보
     */
    @Async
    public void sendErrorNotification(Exception e, HttpServletRequest request) {
        if (!enabled || webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }

        try {
            String message = buildErrorMessage(e, request);
            sendToSlack(message);
        } catch (Exception ex) {
            log.warn("Slack 알림 전송 실패: {}", ex.getMessage());
        }
    }

    /**
     * 에러 메시지를 조립합니다.
     */
    private String buildErrorMessage(Exception e, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        // 헤더
        sb.append(":rotating_light: *서버 에러 발생* :rotating_light:\n\n");

        // 환경 정보
        sb.append("*[환경 정보]*\n");
        sb.append("```\n");
        sb.append("프로파일     : ").append(activeProfile).append("\n");
        sb.append("발생 시각    : ").append(LocalDateTime.now().format(FORMATTER)).append("\n");
        sb.append("```\n\n");

        // 요청 정보
        sb.append("*[요청 정보]*\n");
        sb.append("```\n");
        sb.append("URL          : ").append(buildFullUrl(request)).append("\n");
        sb.append("Method       : ").append(request.getMethod()).append("\n");
        sb.append("Remote IP    : ").append(getClientIp(request)).append("\n");
        sb.append("User-Agent   : ").append(request.getHeader("User-Agent")).append("\n");
        sb.append("```\n\n");

        // 예외 정보
        sb.append("*[예외 정보]*\n");
        sb.append("```\n");
        sb.append("예외 타입    : ").append(e.getClass().getName()).append("\n");
        sb.append("에러 메시지  : ").append(e.getMessage()).append("\n");
        sb.append("```\n\n");

        // 발생 위치 (Root Cause)
        Throwable rootCause = getRootCause(e);
        if (rootCause != e) {
            sb.append("*[Root Cause]*\n");
            sb.append("```\n");
            sb.append("예외 타입    : ").append(rootCause.getClass().getName()).append("\n");
            sb.append("에러 메시지  : ").append(rootCause.getMessage()).append("\n");
            sb.append("```\n\n");
        }

        // 스택 트레이스 (간략화)
        sb.append("*[Stack Trace (요약)]*\n");
        sb.append("```\n");
        sb.append(getShortenedStackTrace(e, 15));
        sb.append("```\n");

        return sb.toString();
    }

    /**
     * Slack 웹훅으로 메시지를 전송합니다.
     */
    private void sendToSlack(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = Map.of("text", message);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(webhookUrl, entity, String.class);

        log.info("Slack 에러 알림 전송 완료");
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
     * 클라이언트 IP 조회 (프록시 고려)
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For에 여러 IP가 있는 경우 첫 번째 IP 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * Root Cause 추출
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    /**
     * 스택 트레이스를 지정된 줄 수만큼 반환
     */
    private String getShortenedStackTrace(Exception e, int maxLines) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        String fullStackTrace = sw.toString();
        String[] lines = fullStackTrace.split("\n");

        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String line : lines) {
            if (count >= maxLines) {
                sb.append("    ... (").append(lines.length - maxLines).append(" more lines)\n");
                break;
            }
            sb.append(line).append("\n");
            count++;
        }

        return sb.toString();
    }
}
