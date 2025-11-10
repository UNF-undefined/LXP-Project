package com.example.projectlxp.user.dto;

import lombok.Getter;

@Getter
public class TokenResponseDTO {

    private String accesstoken;
    private String tokenType = "Bearer"; // 출입증

    public TokenResponseDTO(String accesstoken) {
        this.accesstoken = accesstoken;
    }
}
