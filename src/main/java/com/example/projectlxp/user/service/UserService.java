package com.example.projectlxp.user.service;

import com.example.projectlxp.user.dto.UserJoinRequestDTO;

public interface UserService {

    /*
     * 회원가입 비즈니스 로직
     * @Param requestDTO 회원가입 요청 정보
     * */

    void join(UserJoinRequestDTO requestDTO);

    // 로그인
}
