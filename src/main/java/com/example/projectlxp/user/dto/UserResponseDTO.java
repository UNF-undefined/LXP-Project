package com.example.projectlxp.user.dto;

import com.example.projectlxp.user.entity.Role;
import com.example.projectlxp.user.entity.User;

import lombok.Getter;

@Getter

/*
 *  공용 응답 DTO로
 * */
public class UserResponseDTO {

    private Long userId;
    private String email;
    private String name;
    private Role role;
    private String profileImage;

    public UserResponseDTO(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole();
        this.profileImage = user.getProfileImage();
    }
}
