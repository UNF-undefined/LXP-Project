package com.example.projectlxp.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPasswordUpdateRequestDTO {

    @NotBlank(message = "현재 비밀번호를 입력해주세요. ")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해주세요. ")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{6,}$",
            message = "비밀번호는 6자 이상이며, 영문 소문자와 숫자를 각각 1개 이상 포함해야 합니다.")
    private String newPassword;

    @NotBlank(message = "새 비밀번호를 다시 한번 입력해주세요")
    private String confirmNewPassword;
}
