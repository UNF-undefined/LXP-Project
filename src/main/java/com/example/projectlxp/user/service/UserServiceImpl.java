package com.example.projectlxp.user.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.global.jwt.JwtTokenProvider;
import com.example.projectlxp.user.dto.TokenResponseDTO;
import com.example.projectlxp.user.dto.UserJoinRequestDTO;
import com.example.projectlxp.user.dto.UserLoginRequestDTO;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Override
    @Transactional // DB에 데이터를 저장
    public void join(UserJoinRequestDTO requestDTO) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
        User newUser = requestDTO.toEntity(passwordEncoder);
        userRepository.save(newUser);
    }

    // 로그인 - Controller가 토큰 발급 시 호출
    @Override
    public TokenResponseDTO login(UserLoginRequestDTO requestDTO) {

        // ID/PW로 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getEmail(), requestDTO.getPassword());

        // AuthenticationManager로 인증 (loadUserByUsername 호출 및 비번 비교
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createAccessToken(authentication); // 액세스 토큰 발급
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication); // 리프레쉬 토큰 발급

        // 인증 성공 시 DTO에 두 토큰 생성
        return new TokenResponseDTO(accessToken, refreshToken);
    }
}
