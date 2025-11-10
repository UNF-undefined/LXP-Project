package com.example.projectlxp.course.error;

import com.example.projectlxp.global.error.CustomBusinessException;

public class CourseUpdateDeniedException extends CustomBusinessException {

    public CourseUpdateDeniedException() {
        super("강좌 수정이 거부되었습니다.");
    }
}
