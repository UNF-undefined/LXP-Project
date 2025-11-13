package com.example.projectlxp.user.dto;

import lombok.Getter;

@Getter
public class TokenResponseDTO {

    private String accesstoken;
    private String refreshtoken;
    private String tokenType = "Bearer"; // 출입증

    public TokenResponseDTO(String accesstoken, String refreshtoken, String tokenType) {
        this.accesstoken = accesstoken;
        this.refreshtoken = refreshtoken;
        this.tokenType = "Bearer";
    }
}
