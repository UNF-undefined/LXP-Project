package com.example.projectlxp.course.error;

import com.example.projectlxp.global.error.CustomBusinessException;

public class CourseNotSavedException extends CustomBusinessException {

    public CourseNotSavedException() {
        super("강좌 저장에 실패했습니다.");
    }
}
