package com.example.projectlxp.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.user.dto.UserJoinRequestDTO;
import com.example.projectlxp.user.service.UserService;

import lombok.RequiredArgsConstructor;

// todo restController로
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    /*
     * 회원가입 API
     * */
    @PostMapping("/join")
    public ResponseEntity<String> join(
            // @RequestBody : Form 데이터가 아님 JSON 데이터를 DTO로 변환
            // @valicated : DTO의 @NotBlack 등 유효성 검사 실행
            @Validated @RequestBody UserJoinRequestDTO requestDTO) {

        userService.join(requestDTO);

        return ResponseEntity.ok("회원가입 되셨습니다.");
    }
}
