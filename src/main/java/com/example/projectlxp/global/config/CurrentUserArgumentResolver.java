package com.example.projectlxp.global.config;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.projectlxp.global.annotation.CurrentUserId;
import com.example.projectlxp.user.dto.CustomUserDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    // Resolver 지원 여부 확인
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @CurrentUserID가 붙어있고, 타입이 Long일 때만 작동
        log.info("supportsParameter(MethodParameter parameter)");
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
            log.info("No current user found");
            return null;
        }
        log.info("Resolving Current User Argument");
        /*
         * JwtTokenProvider.getAuthentication()에서
         * Principal에 Long userID를 직접 넣었으므로,
         * (Object) principal을 (Long)으로 캐스팅하여 바로 반환합니다.
         * */
        Object principal = authentication.getPrincipal();

        if (principal instanceof Long) {
            log.info("Current User Argument: " + ((Long) principal));
            return (Long) principal;
        } else if (principal instanceof CustomUserDetails c) {
            return c.getUserId();
        } else if (principal instanceof UserDetails userDetails) { // 💡 MockUser 처리 로직 추가
            // @WithMockUser 사용 시: username("1")을 가져와 Long으로 변환
            String username = userDetails.getUsername();
            try {
                Long userId = Long.valueOf(username);
                log.info("Current User Argument (Mock/UserDetails): " + userId);
                return userId; // Long id로 변환하여 반환
            } catch (NumberFormatException e) {
                log.error("Mock User username is not a valid Long ID: {}", username);
                return null;
            }
        }
        log.info("Current User Argument: " + ((String) principal));
        return null;
    }
}
