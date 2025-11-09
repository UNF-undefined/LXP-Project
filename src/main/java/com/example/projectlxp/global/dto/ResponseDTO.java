package com.example.projectlxp.global.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ResponseDTO {

    private final int status;
    private final String code;
    private final String message;

    public ResponseDTO(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public ResponseDTO(HttpStatus status, String message) {
        this(status.value(), status.name(), message);
    }
}
