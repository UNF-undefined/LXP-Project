package com.example.projectlxp.global.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import com.example.projectlxp.global.config.WithMockUserIdSecurityContextFactory;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserIdSecurityContextFactory.class)
public @interface WithMockUserId {
    long value() default 1L; // 주입할 User ID

    String role() default "ROLE_USER"; // 권한
}
