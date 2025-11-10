package com.example.projectlxp.global.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PageResponse<T> extends BaseResponse<T> {

    private PageDTO page;

    public PageResponse(HttpStatus status, String message, T data, PageDTO page) {
        super(status, message, data);
        this.page = page;
    }

    public static <T> PageResponse<T> success(T data, PageDTO page) {
        return new PageResponse<T>(HttpStatus.OK, HttpStatus.OK.name(), data, page);
    }

    public static <T> PageResponse<T> success(String message, T data, PageDTO page) {
        return new PageResponse<T>(HttpStatus.OK, message, data, page);
    }
}
