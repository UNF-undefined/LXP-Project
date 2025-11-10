package com.example.projectlxp.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.projectlxp.user.entity.Role;
import com.example.projectlxp.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString(exclude = "password") // password 필드를 제외하고 toString()생성
public class UserJoinRequestDTO {

    @NotBlank(message = "이름은 필수 항목입니다.")
    @Size(max = 20, message = "이름은 20자를 초과할 수 없습니다.")
    private String userName;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    // 요구사항 : 6글자 이상 영문 소문자, 숫자 1개 이상 포함
    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{6,}$",
            message = "비밀번호는 6자 이상이며, 영문 소문자와 숫자를 각각 1개 이상 포함해야 합니다.")
    private String password;

    // "STUDENT" 또는 "INSTRUCTOR" 문자열로 받습니다.
    @NotBlank(message = "역할은 필수 항목입니다.")
    @Pattern(
            regexp = "(?i)^(STUDENT|INSTRUCTOR)$",
            message = "역할은 'STUDENT' 또는 'INSTRUCTOR'만 가능합니다.")
    private String role;

    private String profileImage;

    // -- Entity 변환 로직 (Service에서 사용) ---
    public User toEntity(PasswordEncoder passwordEncoder) {
        Role userRole;
        try {
            // "student" -> "STUDENT"
            userRole = Role.valueOf(this.role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 역할(Role)입니다.");
        }

        return User.createUser(
                this.userName,
                this.email,
                passwordEncoder.encode(this.password), // [암호화]
                userRole,
                this.profileImage);
    }
}
