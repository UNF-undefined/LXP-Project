package com.example.projectlxp.user.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.global.jwt.JwtTokenProvider;
import com.example.projectlxp.user.dto.TokenResponseDTO;
import com.example.projectlxp.user.dto.UserJoinRequestDTO;
import com.example.projectlxp.user.dto.UserLoginRequestDTO;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Lazy AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

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

    // 로그인 (loadUserByUsername)
    // spring Security가 인증 시 호출
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // ID 대신 email을 사용하여 , email로 User를 찾음
        User user =
                userRepository
                        .findByEmail(username)
                        .orElseThrow(
                                () ->
                                        new UsernameNotFoundException(
                                                "해당 이메일을 찾을 수 없습니다:" + username));

        // spring Security UserDetails 객체로 변환하여 반환
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail()) // 로그인 ID
                .password(user.getHashedPassword()) // DB에 저장된 암호화된 PW
                .roles(user.getRole().name()) // 권한
                .build();
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

        String accessToken = jwtTokenProvider.createAccessToken(authentication); // 액세스 토큰 발급
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication); // 리프레쉬 토큰 발급

        // 인증 성공 시 , JWT 토큰 생성
        return new TokenResponseDTO(accessToken, refreshToken);
    }
}
