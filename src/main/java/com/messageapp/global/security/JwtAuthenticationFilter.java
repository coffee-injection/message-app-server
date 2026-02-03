package com.messageapp.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messageapp.domain.member.entity.Member;
import com.messageapp.domain.member.repository.MemberRepository;
import com.messageapp.global.error.ErrorCode;
import com.messageapp.global.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT 인증 필터
 *
 * <p>모든 요청에서 JWT 토큰을 검증하고 회원 상태를 확인합니다.</p>
 *
 * <h3>처리 흐름:</h3>
 * <ol>
 *   <li>공개 API 경로는 필터 통과</li>
 *   <li>Authorization 헤더에서 Bearer 토큰 추출</li>
 *   <li>JWT 토큰 유효성 검증</li>
 *   <li>회원 존재 여부 및 활성 상태 확인</li>
 *   <li>검증 실패 시 에러 응답 반환</li>
 * </ol>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /** 인증 없이 접근 가능한 공개 경로 목록 */
    private static final List<String> PUBLIC_PATHS = List.of(
            // 인증 관련
            "/api/v1/auth/login",
            "/api/v1/auth/kakao/**",
            "/api/v1/auth/google/**",
            "/api/v1/auth/signup/complete",
            "/api/v1/auth/refresh",
            // 회원 닉네임 중복 체크
            "/api/v1/member/check-nickname",
            // Swagger
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            // H2 Console
            "/h2-console/**",
            // Health check
            "/actuator/**",
            "/health"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // OPTIONS 요청은 CORS preflight이므로 통과
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 공개 경로는 필터 통과
        if (isPublicPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더 확인
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
            return;
        }

        String token = authHeader.substring(7);

        // JWT 토큰 검증
        if (!jwtTokenProvider.validateToken(token)) {
            sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
            return;
        }

        // 토큰 타입 확인 (temp 토큰은 회원가입 완료 API에서만 사용 가능)
        String tokenType = jwtTokenProvider.getTokenType(token);
        if ("temp".equals(tokenType)) {
            sendErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return;
        }

        // 회원 ID 추출 및 회원 상태 확인
        try {
            Long memberId = jwtTokenProvider.getMemberIdFromToken(token);
            Member member = memberRepository.findById(memberId).orElse(null);

            if (member == null) {
                sendErrorResponse(response, ErrorCode.MEMBER_NOT_FOUND);
                return;
            }

            if (!member.isActive()) {
                sendErrorResponse(response, ErrorCode.MEMBER_NOT_ACTIVE);
                return;
            }

            log.debug("JWT 인증 성공: memberId = {}, path = {}", memberId, requestPath);

        } catch (Exception e) {
            log.error("JWT 토큰 처리 중 오류: {}", e.getMessage());
            sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청 경로가 공개 경로인지 확인합니다.
     *
     * @param requestPath 요청 경로
     * @return 공개 경로이면 true
     */
    private boolean isPublicPath(String requestPath) {
        return PUBLIC_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    /**
     * 에러 응답을 JSON 형식으로 반환합니다.
     *
     * @param response HttpServletResponse
     * @param errorCode 에러 코드
     */
    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", errorCode.getStatus());
        errorResponse.put("message", errorCode.getReason());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
