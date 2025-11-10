package com.example.projectlxp.course.error;

import org.springframework.http.HttpStatus;

import com.example.projectlxp.global.error.CustomBusinessException;

public class CourseCreationDeniedException extends CustomBusinessException {

    public CourseCreationDeniedException() {
        super("강좌를 생성할 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }
}
