package com.example.projectlxp.lecture.service.validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.projectlxp.global.error.CustomBusinessException;

@Component
public class LectureValidator {

    public void validateLectureAuthority(Long authId, Long checkId) {
        if (!authId.equals(checkId)) {
            throw new CustomBusinessException("강의를 관리할 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }
}
