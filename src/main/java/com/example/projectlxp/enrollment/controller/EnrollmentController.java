package com.example.projectlxp.enrollment.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.enrollment.dto.EnrollmentResponseDTO;
import com.example.projectlxp.enrollment.service.EnrollmentService;
import com.example.projectlxp.global.dto.BaseResponse;

@RestController
@RequestMapping("/courses")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/{courseId}/enrollments")
    public BaseResponse<EnrollmentResponseDTO> enrollCourse(
            @PathVariable Long courseId, @RequestParam Long userId) {
        EnrollmentResponseDTO enrollmentResponseDTO =
                enrollmentService.enrollCourse(userId, courseId);
        return BaseResponse.success("수강신청이 성공적으로 완료되었습니다.", enrollmentResponseDTO);
    }
}
