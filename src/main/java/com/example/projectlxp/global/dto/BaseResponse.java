package com.example.projectlxp.global.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BaseResponse<T> extends ResponseDTO {

    private final T data;

    public BaseResponse(HttpStatus status, String message, T data) {
        super(status, message);
        this.data = data;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(HttpStatus.OK, HttpStatus.OK.name(), data);
    }

    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<T>(HttpStatus.OK, message, data);
    }

    public static <T> BaseResponse<T> error(HttpStatus status, String message) {
        return new BaseResponse<T>(status, message, null);
    }
}
