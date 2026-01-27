package com.messageapp.global.auth;

import com.messageapp.global.error.AppException;
import com.messageapp.global.error.ErrorCode;
import com.messageapp.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @LoginMember 어노테이션 처리를 위한 Argument Resolver
 *
 * <p>JWT 토큰에서 회원 ID를 추출하여 컨트롤러 메서드 파라미터에 주입합니다.</p>
 *
 * <p><b>Note:</b> 토큰 유효성 및 회원 상태 검증은 {@link com.messageapp.global.security.JwtAuthenticationFilter}에서
 * 이미 수행되므로, 이 클래스에서는 회원 ID 추출만 담당합니다.</p>
 *
 * @author MessageApp Team
 * @since 1.0
 * @see LoginMember
 * @see com.messageapp.global.security.JwtAuthenticationFilter
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(LoginMember.class);
        boolean isLongType = Long.class.isAssignableFrom(parameter.getParameterType());
        return hasAnnotation && isLongType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                   ModelAndViewContainer mavContainer,
                                   NativeWebRequest webRequest,
                                   WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authHeader = request.getHeader("Authorization");

        // JwtAuthenticationFilter에서 이미 검증되었으므로 헤더가 없는 경우는 발생하지 않아야 함
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        String token = authHeader.substring(7);
        Long memberId = jwtTokenProvider.getMemberIdFromToken(token);

        log.debug("Resolved memberId from token: {}", memberId);

        return memberId;
    }
}
