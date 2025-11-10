package com.example.projectlxp.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomBusinessException extends RuntimeException {

    private final HttpStatus httpStatus;

    /**
     * 예외 메시지만으로 생성합니다.
     *
     * @param message 예외 핸들러가 반환할 메시지
     */
    public CustomBusinessException(String message) {
        // 기본 상태 코드를 400 Bad Request로 설정합니다.
        this(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * 예외 메시지와 HTTP 상태 코드를 함께 생성합니다.
     *
     * @param message 예외 핸들러가 반환할 메시지
     * @param httpStatus 예외 핸들러가 반환할 HTTP 상태
     */
    public CustomBusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
