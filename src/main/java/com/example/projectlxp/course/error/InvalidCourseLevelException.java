package com.example.projectlxp.course.error;

import com.example.projectlxp.global.error.CustomBusinessException;

public class InvalidCourseLevelException extends CustomBusinessException {

    public InvalidCourseLevelException() {
        super("유효하지 않은 Course Level 입니다.");
    }

    public InvalidCourseLevelException(String message) {
        super(message);
    }
}
