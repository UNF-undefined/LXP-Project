package com.example.projectlxp.user.service;

import com.example.projectlxp.user.dto.TokenResponseDTO;
import com.example.projectlxp.user.dto.UserJoinRequestDTO;
import com.example.projectlxp.user.dto.UserLoginRequestDTO;
import com.example.projectlxp.user.dto.UserPasswordUpdateRequestDTO;
import com.example.projectlxp.user.dto.UserResponseDTO;
import com.example.projectlxp.user.dto.UserUpdateRequestDTO;

public interface UserService {

    // 가입
    void join(UserJoinRequestDTO requestDTO);

    // 로그인
    TokenResponseDTO login(UserLoginRequestDTO requestDTO);

    // 정보 조회
    UserResponseDTO getMyInfo(Long userId);

    // 정보수정 (이름, 프로필이미지_
    UserResponseDTO updateMyInfo(Long userId, UserUpdateRequestDTO requestDTO);

    // 정보수정 (패스워드)
    void updatePassword(Long userId, UserPasswordUpdateRequestDTO requestDTO);
}
