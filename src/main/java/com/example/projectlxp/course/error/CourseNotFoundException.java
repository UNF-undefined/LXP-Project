package com.example.projectlxp.course.error;

import org.springframework.http.HttpStatus;

import com.example.projectlxp.global.error.CustomBusinessException;

public class CourseNotFoundException extends CustomBusinessException {

    public CourseNotFoundException() {
        super("요청하신 강좌를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
