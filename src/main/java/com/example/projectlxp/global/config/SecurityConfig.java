package com.example.projectlxp.global.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 세션 정책 추가 (API서버는 STATELESS 권장
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 인증/인가 실패 시 JSON 응답을 위한 핸들러 추가
                .exceptionHandling(
                        exception ->
                                exception
                                        // 인증 실패 401
                                        .authenticationEntryPoint(
                                                (request, response, authException) -> {
                                                    response.setStatus(
                                                            HttpServletResponse.SC_UNAUTHORIZED);
                                                    response.setContentType(
                                                            MediaType.APPLICATION_JSON_VALUE);
                                                    response.setCharacterEncoding("UTF-8");
                                                    // JSON 에러 메세지 반환
                                                    response.getWriter()
                                                            .write(
                                                                    "{\"error\": \"인증이 필요합니다.\", \"status\" : 401}");
                                                })
                                        // 인가 실패 (403 Forbidden)
                                        .accessDeniedHandler(
                                                (request, response, accessDeniedException) -> {
                                                    response.setStatus(
                                                            HttpServletResponse.SC_FORBIDDEN);
                                                    response.setContentType(
                                                            MediaType
                                                                    .APPLICATION_PROBLEM_JSON_VALUE);
                                                    response.setCharacterEncoding("UTF-8");
                                                    response.getWriter()
                                                            .write(
                                                                    "{\"error\" : \"권한이 없습니다.\", \"status\": 403}");
                                                }))
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers("/join", "/login")
                                        .permitAll() // 회원가입 로그인은 누구나
                                        .requestMatchers("/me", "/update")
                                        .authenticated() // 정보조회,수정은 인증필요
                                        .anyRequest()
                                        .authenticated() // 그 외 모든 요청은 인증 필요
                        );
        return http.build(); // http 객체를 build() 해서 반환
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
