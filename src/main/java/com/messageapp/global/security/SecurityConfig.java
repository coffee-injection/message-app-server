package com.messageapp.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 설정 클래스
 *
 * <p>애플리케이션의 보안 설정을 담당합니다.</p>
 *
 * <h3>주요 설정:</h3>
 * <ul>
 *   <li>CORS 설정 - 개발 환경을 위해 모든 Origin 허용</li>
 *   <li>CSRF 비활성화 - REST API이므로 비활성화</li>
 *   <li>보안 헤더 활성화 - X-Frame-Options, XSS Protection, Content-Type-Options</li>
 * </ul>
 *
 * @author MessageApp Team
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Spring Security 필터 체인 설정
     *
     * @param http HttpSecurity 설정 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 설정 중 예외 발생 시
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS 설정 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // URL별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                // Swagger UI 관련 경로는 인증 없이 접근 허용
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // 그 외 모든 요청 허용 (JWT 필터에서 인증 처리)
                .anyRequest().permitAll())

            // CSRF 비활성화 (REST API이므로 불필요)
            .csrf(AbstractHttpConfigurer::disable)

            // 보안 헤더 설정
            .headers(headers -> headers
                // X-Frame-Options: 같은 Origin에서만 iframe 허용 (Clickjacking 방지)
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                // X-XSS-Protection: XSS 공격 감지 시 페이지 로드 차단
                .xssProtection(xss -> xss.headerValue(
                    org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                // X-Content-Type-Options: MIME 타입 스니핑 방지
                .contentTypeOptions(contentTypeOptions -> {})
            );

        return http.build();
    }

    /**
     * CORS 설정 소스 빈 등록
     *
     * <p>개발 환경을 위해 모든 Origin을 허용합니다.
     * 프로덕션 환경에서는 특정 도메인만 허용하도록 변경이 필요합니다.</p>
     *
     * @return CORS 설정이 적용된 CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin 패턴 설정 (개발용: 모든 도메인 허용)
        configuration.setAllowedOriginPatterns(List.of("*"));

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 허용할 헤더 설정 (모든 헤더 허용)
        configuration.setAllowedHeaders(List.of("*"));

        // 인증 정보 포함 허용 (쿠키, Authorization 헤더 등)
        configuration.setAllowCredentials(true);

        // 모든 경로에 CORS 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
