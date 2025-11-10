package com.example.projectlxp.user.dto;

import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor // 테스트용
public class UserUpdateRequestDTO {

    @Size(max = 20, message = "이름은 20자를 초과할 수 없습니다.")
    private String name;

    private String profileImage;
}
