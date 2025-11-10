package com.example.projectlxp.user.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUserDetails extends User {

    private final Long userId;

    public CustomUserDetails(
            Long userId,
            String email,
            String password,
            Collection<? extends GrantedAuthority> authorities) {

        // 부모클래스 생성자 호출
        super(email, password, authorities);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
