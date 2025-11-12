package com.example.projectlxp.course.error;

import com.example.projectlxp.global.error.CustomBusinessException;

public class InvalidCourseSortException extends CustomBusinessException {

    public InvalidCourseSortException(String message) {
        super(message);
    }

    public InvalidCourseSortException() {
        super("유효하지 않은 Course Sort 입니다.");
    }
}
