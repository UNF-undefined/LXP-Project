package com.example.projectlxp.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.global.annotation.CurrentUserId;
import com.example.projectlxp.user.dto.CustomUserDetails;
import com.example.projectlxp.user.dto.TokenResponseDTO;
import com.example.projectlxp.user.dto.UserJoinRequestDTO;
import com.example.projectlxp.user.dto.UserLoginRequestDTO;
import com.example.projectlxp.user.dto.UserPasswordUpdateRequestDTO;
import com.example.projectlxp.user.dto.UserResponseDTO;
import com.example.projectlxp.user.dto.UserUpdateRequestDTO;
import com.example.projectlxp.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입 API
    @PostMapping("/join")
    public ResponseEntity<String> join(
            // @RequestBody : Form 데이터가 아님 JSON 데이터를 DTO로 변환
            // @valicated : DTO의 @NotBlack 등 유효성 검사 실행
            @Validated @RequestBody UserJoinRequestDTO requestDTO) {

        userService.join(requestDTO);
        return ResponseEntity.ok("회원가입 되셨습니다.");
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(
            @Validated @RequestBody UserLoginRequestDTO requestDTO) {

        TokenResponseDTO tokenDTO = userService.login(requestDTO);
        return ResponseEntity.ok(tokenDTO);
    }

    // 로그아웃은 Security에 의해서 자동으로 API 매핑 됩니다.
    // 로그인 = Manual , 로그아웃 = Automatic

    // 정보조회 API
    @GetMapping("/me")
    public ResponseEntity<Object> getMyInfo(@CurrentUserId Long userId) {

        UserResponseDTO responseDTO = userService.getMyInfo(userId);
        return ResponseEntity.ok(responseDTO);
    }

    // 정보수정 API Put = 전체수정 , Path = 부분수정 방식은 비슷함
    @PatchMapping("/update")
    public ResponseEntity<UserResponseDTO> updateMyInfo(
            @CurrentUserId Long userId, // 누가 수정할지 (토큰으로 식별)
            @Validated @RequestBody UserUpdateRequestDTO requestDTO // 무엇을 수정할지
            ) {
        UserResponseDTO responseDTO = userService.updateMyInfo(userId, requestDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @CurrentUserId Long userId,
            @Validated @RequestBody UserPasswordUpdateRequestDTO requestDTO) {

        userService.updatePassword(userId, requestDTO);

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다. ");
    }

    // 회원 탈퇴 API
    @DeleteMapping("/withdraw")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 nO Content반환
    public ResponseEntity<String> withdrawUser(
            // Security Context에서 인증된 사용자 ID를 가져옴
            @AuthenticationPrincipal CustomUserDetails principal) {

        userService.withdraw(principal.getUserId());
        return ResponseEntity.ok("성공적으로 탈퇴되었습니다. ");
    }
}
