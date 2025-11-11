package com.example.projectlxp.enrollment.service.validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.projectlxp.enrollment.entity.Enrollment;
import com.example.projectlxp.global.error.CustomBusinessException;

@Component
public class EnrollmentValidator {
    public void validateOwnership(Long userId, Enrollment enrollment) {
        if (!enrollment.getUser().getId().equals(userId)) {
            throw new CustomBusinessException(
                    "수강신청을 숨길 권한이 없습니다. 회원 ID: " + userId + ", 수강신청 ID: " + enrollment.getId(),
                    HttpStatus.FORBIDDEN);
        }
    }
}
