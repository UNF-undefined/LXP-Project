package com.example.projectlxp.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.user.dto.UserJoinRequestDTO;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Override
    @Transactional // DB에 데이터를 저장
    public void join(UserJoinRequestDTO requestDTO) {

        // -- 테스트 코드 ---

        // 이메일 중복 체크
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
        User newUser = requestDTO.toEntity(passwordEncoder);
        userRepository.save(newUser);
    }

    // 로그인
    /*
     * spring Security가 로그인을 처리할 때 호출하는 메서드
     * @param username (로그인 시도하는 ID, 여기서는 email)
     * @return UserDetails (Spring Security가 사용하는 User정보)
     * @throws UsernameNotFoundException
     * */
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
}
