package com.example.projectlxp.category.error;

import org.springframework.http.HttpStatus;

import com.example.projectlxp.global.error.CustomBusinessException;

public class CategoryNotFoundException extends CustomBusinessException {

    public CategoryNotFoundException() {
        super("존재하지 않는 카테고리 ID입니다.", HttpStatus.NOT_FOUND);
    }
}
