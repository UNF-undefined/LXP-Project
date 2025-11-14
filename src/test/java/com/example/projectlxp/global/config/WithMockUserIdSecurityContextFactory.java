package com.example.projectlxp.global.config;

import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.example.projectlxp.global.annotation.WithMockUserId;

public class WithMockUserIdSecurityContextFactory
        implements WithSecurityContextFactory<WithMockUserId> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserId annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // 주석에서 언급된 대로 Principal에 Long ID를 직접 넣습니다.
        Long userId = annotation.value();

        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(annotation.role()));

        // principal 자리에 Long 타입의 userId를 전달
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null, // credentials
                        authorities);

        context.setAuthentication(auth);
        return context;
    }
}
