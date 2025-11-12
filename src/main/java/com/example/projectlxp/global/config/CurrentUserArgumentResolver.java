package com.example.projectlxp.global.config;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    // Resolver 지원 여부 확인
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @CurrentUserID가 붙어있고, 타입이 Long일 때만 작동
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && parameter.getParameterType().equals(Long.class);
    }

    // 값 추출 및 주입
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavAndViewContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory)
            throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        /*
         * JwtTokenProvider.getAuthentication()에서
         * Principal에 Long userID를 직접 넣었으므로,
         * (Object) principal을 (Long)으로 캐스팅하여 바로 반환합니다.
         * */
        Object principal = authentication.getPrincipal();

        if (principal instanceof Long) {
            return (Long) principal;
        }

        return null;
    }
}
