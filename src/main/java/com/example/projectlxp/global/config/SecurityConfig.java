package com.example.projectlxp.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 비밀번호 암호화 도구 (PasswordEncoder)를 Bean으로 등록

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 보안 규칙 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (API서버)
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers("/**")
                                        .permitAll() // 회원가입 경로는 누구나
                                        .anyRequest()
                                        .authenticated() // 그 외 모든 요청은 인증 필요
                        );
        return http.build(); // http 객체를 build() 해서 반환
    }
}
