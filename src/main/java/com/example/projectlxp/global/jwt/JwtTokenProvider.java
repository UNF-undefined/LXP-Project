package com.example.projectlxp.global.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.example.projectlxp.user.dto.CustomUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String USER_ID_KEY = "userId";
    private final UserDetailsService userDetailsService;

    // applcation-locla.yml 에서 설정 값 가져오기
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
            // Refresh token 만료 시간 주입
            @Value("${jwt.refresh-token-expiration-ms}") long freshTokenExpirationMs,
            UserDetailsService UserDetailsService) {

        // yml의 secret 문자열을 SecretKry 객체로 변환
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        // 필드 값 할당
        this.refreshTokenExpirationMs = freshTokenExpirationMs;
        this.userDetailsService = UserDetailsService;
    }

    // 토큰 생성 메서드 (로그인 성공 시 호출됨)
    public String createAccessToken(Authentication authentication) {

        // Authentication 객체에서 Principal을 꺼냅니다.
        Object principal = authentication.getPrincipal();

        // Principal을 CustomUserDetails로 형변환
        CustomUserDetails userDetails = (CustomUserDetails) principal;

        // CustomUserDetails에 저장해둔 PK를 꺼냄
        Long userId = userDetails.getUserId(); // PK(id)가져오기 성공

        // Authentication 객체에서 권한 정보 가져오기
        String authorities =
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(
                                authority -> { // 여기에서 ROLE_ 접두사를 확인하고 추가
                                    if (authority.startsWith("ROLE_")) {
                                        return authority;
                                    }
                                    return "ROLE_" + authority;
                                })
                        .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenExpirationMs); // 만료시간(짧음)

        return Jwts.builder()
                .setSubject(authentication.getName()) // 로그인 할 아이디, email
                .claim(AUTHORITIES_KEY, authorities) // 권한정보
                .claim(USER_ID_KEY, userId) // 가져온 PK를 클레임에 추가
                .claim("roles", authorities)
                .signWith(key, SignatureAlgorithm.HS512) // 비밀키로 서명
                .setExpiration(validity) // 만료시간 설정
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenExpirationMs); // 만료시간(김)

        return Jwts.builder()
                .setSubject(authentication.getName()) // 로그인할 아이디 email
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // 인증 정보 추출 메서드
    public Authentication getAuthentication(String token) {
        Claims claims =
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        // AUTHORITIES_KEY 권한정보 Null 체크 및 안전한 처리

        Object authoritiesObject = claims.get(AUTHORITIES_KEY);

        Collection<? extends GrantedAuthority> authorities;

        if (authoritiesObject == null) {
            // 널일 경우 : 권한이 없음을 나타내는 빈 목록 사용
            authorities = List.of();
        } else {
            // 널이 아닐 경우 : 안전하게 String으로 변환 후 처리
            authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
        }
        Long userId = claims.get(USER_ID_KEY, Long.class);
        String email = claims.get(Claims.SUBJECT).toString();
        String dummyPassword = "";

        CustomUserDetails principalDetails =
                new CustomUserDetails(
                        userId,
                        email,
                        dummyPassword, // JWT 인증 시 비밀번호는 사용하지 않습니다.
                        authorities);

        // 4. Authentication 객체 생성 및 반환
        return new UsernamePasswordAuthenticationToken(
                principalDetails, // Principal로 CustomUserDetails 객체 사용
                token, // Credentials (토큰)
                authorities // 토큰에서 추출한 권한 목록
                );
    }

    // 토큰 검증 메서드

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
