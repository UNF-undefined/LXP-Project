package com.example.projectlxp.global.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
    private final long freshTokenExpirationMs;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String USER_ID_KEY = "userId";

    // applcation-locla.yml 에서 설정 값 가져오기
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
            // Refresh token 만료 시간 주입
            @Value("${jwt.refresh-token-expiration-ms}") long freshTokenExpirationMs) {

        // yml의 secret 문자열을 SecretKry 객체로 변환
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        // 필드 값 할당
        this.freshTokenExpirationMs = freshTokenExpirationMs;
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
                        .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenExpirationMs); // 만료시간(짧음)

        return Jwts.builder()
                .setSubject(authentication.getName()) // 로그인 할 아이디, email
                .claim(AUTHORITIES_KEY, authorities) // 권한정보
                .claim(USER_ID_KEY, userId) // 가져온 PK를 클레임에 추가
                .signWith(key, SignatureAlgorithm.HS512) // 비밀키로 서명
                .setExpiration(validity) // 만료시간 설정
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.freshTokenExpirationMs); // 만료시간(김)

        return Jwts.builder()
                .setSubject(authentication.getName()) // 로그인할 아이디 email
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /*
     * 토큰에서 인증 정보 추출
     * */
    public Authentication getAuthentication(String token) {
        // 토큰을 복호화하여 claims을 꺼냄
        Claims claims =
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
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
