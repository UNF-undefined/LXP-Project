package com.example.projectlxp.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.user.dto.UserJoinRequestDTO;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
