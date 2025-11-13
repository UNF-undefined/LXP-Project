package com.example.projectlxp.user.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectlxp.user.dto.CustomUserDetails;
import com.example.projectlxp.user.entity.User;
import com.example.projectlxp.user.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    // 생성자 이름 수정
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        User userEntity;

        try {
            Long userId = Long.parseLong(identifier);

            userEntity =
                    userRepository
                            .findById(userId)
                            .orElseThrow(
                                    () ->
                                            new UsernameNotFoundException(
                                                    "해당 ID의 사용자를 찾을 수 없습니다." + identifier));

        } catch (NumberFormatException e) {
            // 숫자로 변환이 안되면 (로그인 시의 이메일이나 사용자명을 가정) 이메일/이름으로 조회
            userEntity =
                    userRepository
                            .findByEmail(identifier)
                            .orElseThrow(
                                    () ->
                                            new UsernameNotFoundException(
                                                    "해당 이메일의 사용자를 찾을 수 없습니다." + identifier));
        }

        return new CustomUserDetails(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getHashedPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(userEntity.getRole().name())));
    }
}
