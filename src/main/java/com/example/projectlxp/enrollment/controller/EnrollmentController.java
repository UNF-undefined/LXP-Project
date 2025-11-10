package com.example.projectlxp.enrollment.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectlxp.enrollment.dto.request.CreateEnrollmentRequestDTO;
import com.example.projectlxp.enrollment.dto.response.CreateEnrollmentResponseDTO;
import com.example.projectlxp.enrollment.dto.response.PagedEnrolledCourseDTO;
import com.example.projectlxp.enrollment.service.EnrollmentService;
import com.example.projectlxp.global.dto.BaseResponse;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public BaseResponse<CreateEnrollmentResponseDTO> enrollCourse(
            @RequestParam Long userId, @Valid @RequestBody CreateEnrollmentRequestDTO requestDTO) {
        CreateEnrollmentResponseDTO createEnrollmentResponseDTO =
                enrollmentService.enrollCourse(userId, requestDTO);
        return BaseResponse.success("수강신청이 성공적으로 완료되었습니다.", createEnrollmentResponseDTO);
    }

    @GetMapping("/my")
    public BaseResponse<PagedEnrolledCourseDTO> getMyCourses(
            @RequestParam Long userId, @PageableDefault Pageable pageable) {
        PagedEnrolledCourseDTO pagedEnrolledCourseDTO =
                enrollmentService.getMyEnrolledCourses(userId, pageable);
        return BaseResponse.success("수강중인 강좌 목록 조회를 성공했습니다.", pagedEnrolledCourseDTO);
    }
}
